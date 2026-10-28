package com.jamiedev.bygone.common.commands;

import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.common.weather.weather_types.WeatherProperties;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import com.jamiedev.bygone.core.registry.BGDimensions;
import com.jamiedev.bygone.forge.core.registry.BGRegistriesForge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Collectors;

public class BygoneWeatherCommand {
    private static final DynamicCommandExceptionType UNKNOWN_WEATHER = new DynamicCommandExceptionType(
            id -> Component.literal("Unknown weather type: " + id));
    private static final DynamicCommandExceptionType UNKNOWN_PROPERTY = new DynamicCommandExceptionType(
            name -> Component.literal("Unknown property: " + name));
    private static final DynamicCommandExceptionType INVALID_CAST_EXCEPTION = new DynamicCommandExceptionType(
            input -> Component.translatable("commands.bygone.weather.incompatible_cast", input));
    private static final SimpleCommandExceptionType BYGONE_LEVEL_NOT_LOADED_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.bygone.weather.dimension_not_loaded"));
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("bygone_weather")
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.argument("weather_type", ResourceLocationArgument.id())
                        .suggests((c, b) -> SharedSuggestionProvider.suggestResource(weatherRegistry().getKeys(), b))
                        .then(Commands.literal("start").executes(BygoneWeatherCommand::startWeather))
                        .then(Commands.literal("clear").executes(BygoneWeatherCommand::clearWeather))
                        .then(Commands.literal("property").then(
                                Commands.argument("property", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                Optional<WeatherType> weatherType = getWeatherType(ctx);
                                                if (weatherType.isPresent()) {
                                                    for (String property : weatherType.get().getProperties().stream()
                                                            .map(WeatherProperties.WeatherProperty::getIdentifier)
                                                            .collect(Collectors.toSet())) {
                                                        builder.suggest(property);
                                                    }
                                                }
                                            } catch (CommandSyntaxException ignored) {}
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.literal("query").executes(BygoneWeatherCommand::queryPropertyValue))
                                        .then(Commands.literal("set").then(
                                                Commands.argument("value", StringArgumentType.word())
                                                        .executes(BygoneWeatherCommand::setPropertyValue)))
                        ))
                )
        );
    }

    private static IForgeRegistry<WeatherType.Factory> weatherRegistry() {
        return BGRegistriesForge.WEATHER_TYPES_FORGE.get();
    }
    
    private static WeatherType.Factory getFactory(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "weather_type");
        WeatherType.Factory factory = weatherRegistry().getValue(id);
        if (factory == null) throw UNKNOWN_WEATHER.create(id);
        return factory;
    }
    
    private static String weatherName(CommandContext<CommandSourceStack> ctx) {
        return ResourceLocationArgument.getId(ctx, "weather_type").toString();
    }
    
    private static BygoneWeather getWeatherSystem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel bygoneLevel = ctx.getSource().getServer().getLevel(BGDimensions.BYGONE_LEVEL_KEY);
        if (bygoneLevel == null) throw BYGONE_LEVEL_NOT_LOADED_EXCEPTION.create();
        return BygoneWeather.getOrDefault(bygoneLevel);
    }
    
    @SuppressWarnings("rawtypes")
    private static Optional<WeatherType> getWeatherType(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BygoneWeather weatherSystem = getWeatherSystem(ctx);
        WeatherType.Factory factory = getFactory(ctx);
        return weatherSystem.getWeatherType(factory.getLocation());
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Optional<WeatherProperties.WeatherProperty<T>> getProperty(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Optional<WeatherType> weatherType = getWeatherType(ctx);
        if (weatherType.isEmpty()) return Optional.empty();
        String propertyName = StringArgumentType.getString(ctx, "property");
        WeatherProperties.WeatherProperty<T> property = weatherType.get().getProperty(propertyName);
        if (property == null) throw UNKNOWN_PROPERTY.create(propertyName);
        return Optional.of(property);
    }
    
    public static int startWeather(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Optional<WeatherType> weatherType = getWeatherType(ctx);
        if (weatherType.isEmpty()) return 0;
        
        weatherType.get().startWeather();
        
        String name = weatherName(ctx);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.bygone.weather.start", name), true);
        return 1;
    }
    
    public static int clearWeather(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Optional<WeatherType> weatherType = getWeatherType(ctx);
        if (weatherType.isEmpty()) return 0;
        
        weatherType.get().clearWeather();
        
        String name = weatherName(ctx);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.bygone.weather.clear", name), true);
        return 1;
    }
    
    public static int queryPropertyValue(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Optional<WeatherProperties.WeatherProperty<Object>> optional = getProperty(ctx);
        if (optional.isEmpty()) return 0;
        
        WeatherProperties.WeatherProperty<Object> property = optional.get();
        String name = weatherName(ctx);
        ctx.getSource().sendSuccess(() -> Component.translatable(
                "commands.bygone.weather.query_property",
                property.getIdentifier(), name, property.getValue().toString()), true);
        return 1;
    }
    
    public static int setPropertyValue(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String propertyValue = StringArgumentType.getString(ctx, "value");
        Optional<WeatherProperties.WeatherProperty<Object>> optional = getProperty(ctx);
        if (optional.isEmpty()) return 0;
        
        WeatherProperties.WeatherProperty<Object> property = optional.get();
        String previous = property.getValue().toString();
        String name = weatherName(ctx);
        
        attemptSetValue(property, propertyValue);
        
        ctx.getSource().sendSuccess(() -> Component.translatable(
                "commands.bygone.weather.set_property",
                property.getIdentifier(), name, property.getValue().toString(), previous), true);
        return 1;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> void attemptSetValue(WeatherProperties.WeatherProperty<T> property, String propertyValue) throws CommandSyntaxException {
        Class<T> valueClass = (Class<T>) property.getValue().getClass();
        try {
            Method method = valueClass.getMethod("valueOf", String.class);
            property.setValue(valueClass.cast(method.invoke(null, propertyValue)));
        } catch (Exception e) {
            throw INVALID_CAST_EXCEPTION.create(valueClass.getSimpleName());
        }
    }
}