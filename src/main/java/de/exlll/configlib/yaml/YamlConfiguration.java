package de.exlll.configlib.yaml;

import de.exlll.configlib.Comments;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.ConfigurationSource;
import de.exlll.configlib.ConfigurationStoreException;
import de.exlll.configlib.format.FieldNameFormatters;
import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.utils.ReflectionUtils;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class YamlConfiguration extends Configuration<YamlConfiguration> {
    private final YamlSource source;

    protected YamlConfiguration(Path path, YamlProperties properties) {
        super(properties);
        this.source = new YamlSource(path, properties);
    }

    protected YamlConfiguration(Path path) {
        this(path, YamlProperties.DEFAULT);
    }

    @Override
    protected final ConfigurationSource<YamlConfiguration> getSource() {
        return source;
    }

    @Override
    protected final YamlConfiguration getThis() {
        return this;
    }

    public final void loadAndSave() {
        try {
            load();
            save();
        } catch (ConfigurationStoreException e) {
            if (e.getCause() instanceof NoSuchFileException) {
                try {
                    postLoad();
                    save();
                } catch (Exception ex) {
                    if (source != null) {
                        OSAPI.getAPI().getLogger().severe("Failed to load and save configuration file '" + source.getConfigPath() + "'. Please check the file for errors, use a YAML validator like https://codebeautify.org/yaml-validator");
                    }
                    throw ex;
                }
            } else {
                throw e;
            }
        } catch (Exception e) {
            if (source != null) {
                OSAPI.getAPI().getLogger().severe("Failed to load and save configuration file '" + source.getConfigPath() + "'. Please check the file for errors, use a YAML validator like https://codebeautify.org/yaml-validator");
            }
            throw e;
        }
    }

    Comments getComments() {
        return comments;
    }

    public static class YamlProperties extends Properties {
        public static final YamlProperties DEFAULT = builder().setFormatter(FieldNameFormatters.LOWER_HYPHON).build();
        private final List<String> prependedComments;
        private final List<String> appendedComments;
        private final BaseConstructor constructor;
        private final Representer representer;
        private final DumperOptions options;
        private final Resolver resolver;

        protected YamlProperties(Builder<?> builder) {
            super(builder);
            this.prependedComments = builder.prependedComments;
            this.appendedComments = builder.appendedComments;
            this.constructor = builder.constructor;
            this.representer = builder.representer;
            this.options = builder.options;
            this.resolver = builder.resolver;
        }

        public static Builder<?> builder() {
            return new Builder() {
                @Override
                protected Builder<?> getThis() {
                    return this;
                }
            };
        }

        public final List<String> getPrependedComments() {
            return prependedComments;
        }

        public final List<String> getAppendedComments() {
            return appendedComments;
        }

        public final BaseConstructor getConstructor() {
            return constructor;
        }

        public final Representer getRepresenter() {
            return representer;
        }

        public final DumperOptions getOptions() {
            return options;
        }

        public final Resolver getResolver() {
            return resolver;
        }

        public static abstract class Builder<B extends Builder<B>>
                extends Properties.Builder<B> {
            private List<String> prependedComments = Collections.emptyList();
            private List<String> appendedComments = Collections.emptyList();
            private BaseConstructor constructor = this.newConstructor();

            protected YamlConstructor newConstructor() {
                if (ReflectionUtils.hasNoArgConstructor(YamlConstructor.class)) {
                    return ReflectionUtils.newInstance(YamlConstructor.class);
                }

                return ReflectionUtils.newInstance(YamlConstructor.class, new LoaderOptions());
            }

            private Representer representer = this.newRepresenter();

            protected Representer newRepresenter() {
                if (ReflectionUtils.hasNoArgConstructor(YamlRepresenter.class)) {
                    return ReflectionUtils.newInstance(YamlRepresenter.class);
                }
                return ReflectionUtils.newInstance(YamlRepresenter.class, new DumperOptions());
            }

            private DumperOptions options = new DumperOptions();
            private Resolver resolver = new Resolver();

            protected Builder() {
                options.setIndent(2);
                options.setDefaultFlowStyle(FlowStyle.BLOCK);
            }

            /**
             * Sets the comments prepended to a configuration.
             *
             * @param prependedComments List of comments that are prepended
             * @return this {@code Builder}
             * @throws NullPointerException if {@code prependedComments ist null}
             */
            public final B setPrependedComments(List<String> prependedComments) {
                this.prependedComments = Objects.requireNonNull(prependedComments);
                return getThis();
            }

            /**
             * Sets the comments appended to a configuration.
             *
             * @param appendedComments List of comments that are appended
             * @return this {@code Builder}
             * @throws NullPointerException if {@code appendedComments ist null}
             */
            public final B setAppendedComments(List<String> appendedComments) {
                this.appendedComments = Objects.requireNonNull(appendedComments);
                return getThis();
            }

            /**
             * Sets the {@link BaseConstructor} used by the underlying YAML-parser.
             *
             * @param constructor {@code BaseConstructor} used by YAML-parser.
             * @return this {@code Builder}
             * @throws NullPointerException if {@code constructor ist null}
             * @see <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation">snakeyaml-Documentation</a>
             */
            public final B setConstructor(BaseConstructor constructor) {
                this.constructor = Objects.requireNonNull(constructor);
                return getThis();
            }

            /**
             * Sets the {@link Representer} used by the underlying YAML-parser.
             *
             * @param representer {@code Representer} used by YAML-parser.
             * @return this {@code Builder}
             * @throws NullPointerException if {@code representer ist null}
             * @see <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation">snakeyaml-Documentation</a>
             */
            public final B setRepresenter(Representer representer) {
                this.representer = Objects.requireNonNull(representer);
                return getThis();
            }

            /**
             * Sets the {@link DumperOptions} used by the underlying YAML-parser.
             *
             * @param options {@code DumperOptions} used by YAML-parser.
             * @return this {@code Builder}
             * @throws NullPointerException if {@code options ist null}
             * @see <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation">snakeyaml-Documentation</a>
             */
            public final B setOptions(DumperOptions options) {
                this.options = Objects.requireNonNull(options);
                return getThis();
            }

            /**
             * Sets the {@link Resolver} used by the underlying YAML-parser.
             *
             * @param resolver {@code Resolver} used by YAML-parser.
             * @return this {@code Builder}
             * @throws NullPointerException if {@code resolver ist null}
             * @see <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation">snakeyaml-Documentation</a>
             */
            public final B setResolver(Resolver resolver) {
                this.resolver = Objects.requireNonNull(resolver);
                return getThis();
            }

            /**
             * Builds a new {@code YamlProperties} instance using the values set.
             *
             * @return new {@code YamlProperties} instance
             */
            public YamlProperties build() {
                return new YamlProperties(this);
            }
        }
    }

    /**
     * @param list List of numbers
     * @param type Type of the number
     * @param <T>  Type of the number
     * @return a list with the numbers converted to the given type
     * @apiNote This method is intented to ensure translated numbers are of the right type
     */
    protected <T> List<T> fixNumberList(List<? extends Number> list, Class<T> type) {
        List<T> fixedList = new ArrayList<>();
        for (Number number : list) {
            if (type == Integer.class || type == int.class) {
                fixedList.add((T) Integer.valueOf(number.intValue()));
            } else if (type == Long.class || type == long.class) {
                fixedList.add((T) Long.valueOf(number.longValue()));
            } else if (type == Double.class || type == double.class) {
                fixedList.add((T) Double.valueOf(number.doubleValue()));
            } else if (type == Float.class || type == float.class) {
                fixedList.add((T) Float.valueOf(number.floatValue()));
            } else if (type == Short.class || type == short.class) {
                fixedList.add((T) Short.valueOf(number.shortValue()));
            } else if (type == Byte.class || type == byte.class) {
                fixedList.add((T) Byte.valueOf(number.byteValue()));
            }
        }
        return fixedList;
    }

    /**
     * @param map  Map of numbers
     * @param type Type of the number
     * @param <T>  Type of the number
     * @return a map with the numbers converted to the given type
     * @apiNote This method is intented to ensure translated numbers are of the right type
     */
    protected <A, T> Map<A, T> fixNumberMap(Map<A, ? extends Number> map, Class<T> type) {
        Map<A, T> fixedMap = new HashMap<>();
        for (Map.Entry<A, ? extends Number> entry : map.entrySet()) {
            if (type == Integer.class || type == int.class) {
                fixedMap.put(entry.getKey(), (T) Integer.valueOf(entry.getValue().intValue()));
            } else if (type == Long.class || type == long.class) {
                fixedMap.put(entry.getKey(), (T) Long.valueOf(entry.getValue().longValue()));
            } else if (type == Double.class || type == double.class) {
                fixedMap.put(entry.getKey(), (T) Double.valueOf(entry.getValue().doubleValue()));
            } else if (type == Float.class || type == float.class) {
                fixedMap.put(entry.getKey(), (T) Float.valueOf(entry.getValue().floatValue()));
            } else if (type == Short.class || type == short.class) {
                fixedMap.put(entry.getKey(), (T) Short.valueOf(entry.getValue().shortValue()));
            } else if (type == Byte.class || type == byte.class) {
                fixedMap.put(entry.getKey(), (T) Byte.valueOf(entry.getValue().byteValue()));
            }
        }
        return fixedMap;

    }

}
