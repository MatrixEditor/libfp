/*
 * MIT License
 *
 * Copyright (c) 2024 MatrixEditor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.libfp.cli.xml;

import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.CHAStrategy;
import io.github.libfp.cli.Config;
import io.github.libfp.profile.IIntegration;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.profile.features.IStep;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.profile.manager.IExtension;
import io.github.libfp.profile.manager.RetentionPolicy;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.SimpleThresholdConfig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class XMLProfile extends DefaultHandler
{

    public final List<ExtensionConfig> extensions = new LinkedList<>();
    public String name = null;
    public String extension = null;
    public String target = null;
    public Class<?> profileClass = CHAProfile.class;
    public ILFactory factory = null;
    public BytecodeNormalizer normalizer = null;
    public IIntegration integration = null;
    public CHAStrategy strategy = new CHAStrategy();

    public SimpleThresholdConfig config = new SimpleThresholdConfig();

    private ExtensionConfig lastExtension = null;

    public static XMLProfile parse(String path) throws Exception
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLProfile profile = new XMLProfile();
        try (InputStream is = new FileInputStream(Config.clean(path))) {
            factory.newSAXParser().parse(is, profile);
        }
        return profile;
    }

    public static Class<?> getClass(String name)
    {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            System.out.println(
                    "\u001B[31m[E] Could not find or load class: '" + name +
                            "'\u001B[0m");
            System.exit(1);
            return null;
        }
    }

    public static String getValue(Attributes attributes, String name,
                                  String defaultValue)
    {
        String value = attributes.getValue(name);
        return value == null ? defaultValue : value;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException
    {
        switch (qName.toLowerCase()) {
            case "profile": {
                name = attributes.getValue("name");
                extension = attributes.getValue("file-extension");
                target = attributes.getValue("target");
                break;
            }
            case "impl": {
                profileClass = getClass(attributes.getValue("class"));
                break;
            }
            case "il-factory": {
                factory = Config.getInstance(
                        attributes.getValue("class"),
                        ILFactory.class,
                        new Class<?>[]{}
                );
                break;
            }
            case "normalizer": {
                if (factory != null) {
                    normalizer = Config.getInstance(
                            attributes.getValue("class"),
                            BytecodeNormalizer.class,
                            new Class<?>[]{ILFactory.class},
                            factory
                    );
                } else {
                    throw new SAXException("ILFactory is not set");
                }
                break;
            }
            case "integration": {
                integration = Config.getInstance(
                        attributes.getValue("class"),
                        IIntegration.class,
                        new Class<?>[]{}
                );
                break;
            }
            case "similarity": {
                //noinspection rawtypes
                Class l = getClass(attributes.getValue("target"));
                ISimilarityStrategy<?> s = Config.getInstance(
                        attributes.getValue("class"),
                        ISimilarityStrategy.class,
                        new Class<?>[]{}
                );
                if (s != null) {
                    strategy.with(l, s);
                }
                break;
            }
            case "layer": {
                Class l = getClass(attributes.getValue("target"));
                Object o = Config.getInstance(
                        attributes.getValue("class"),
                        Object.class,
                        new Class<?>[]{}
                );
                if (o != null) {
                    strategy.with(l, (ISimilarityStrategy<?>) o);
                    strategy.with(l, (IStep<?, ? extends ManagedProfile>) o);
                }
                break;
            }
            case "extension": {
                lastExtension = new ExtensionConfig(
                        attributes.getValue("class"),
                        RetentionPolicy.valueOf(
                                getValue(attributes, "retention", "RUNTIME")),
                        getValue(attributes, "enabled",
                                "true").equalsIgnoreCase("true"),
                        new LinkedList<>());
                extensions.add(lastExtension);
                break;
            }
            case "arg": {
                if (lastExtension != null) {
                    lastExtension.args.add(
                            new Arg(attributes.getValue("class"),
                                    attributes.getValue("impl")));
                }
                break;
            }

            case "threshold": {
                config.set(getClass(attributes.getValue("class")),
                        Double.parseDouble(attributes.getValue("value")));
            }
            default:
                break;
        }
    }

    public String toANSIString(XMLProfileProvider provider)
    {
        return """
                \u001B[36mImported profile from XML:\u001B[0m
                    | name: \u001B[92m'%s'\u001B[0m
                    | extension: \u001B[92m'%s'\u001B[0m
                    | target: \u001B[92m'%s'\u001B[0m
                    | profileClass: \u001B[95m%s\u001B[0m
                    | il-factory: \u001B[95m%s\u001B[0m
                    | normalizer: \u001B[95m%s\u001B[0m
                    | integration: \u001B[95m%s\u001B[0m
                    | base-strategy: %s
                """.formatted(
                name,
                extension,
                target,
                profileClass.getName(),
                factory != null ? factory.getClass().getName() : "null",
                normalizer != null ? normalizer.getClass().getName() : "null",
                integration.getClass().getName(),
                provider.getStrategy());
    }

    record Arg(String type, String impl)
    {
    }

    public record ExtensionConfig(String type, RetentionPolicy policy,
                                  boolean enabled,
                                  List<Arg> args)
    {


        public IExtension newInstance()
        {
            if (args.isEmpty()) {
                return Config.getInstance(type, IExtension.class,
                        new Class<?>[]{});
            } else {
                Object[] argv = new Object[args.size()];
                Class<?>[] argTypes = new Class<?>[args.size()];
                for (int i = 0; i < argv.length; i++) {
                    argTypes[i] = XMLProfile.getClass(args.get(i).type);
                    argv[i] = Config.getInstance(args.get(i).impl, argTypes[i],
                            new Class<?>[]{});
                }
                return Config.getInstance(type, IExtension.class, argTypes,
                        argv);
            }
        }
    }
}
