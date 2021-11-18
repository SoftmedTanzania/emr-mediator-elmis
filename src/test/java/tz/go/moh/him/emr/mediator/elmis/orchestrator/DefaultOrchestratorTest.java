package tz.go.moh.him.emr.mediator.elmis.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.emr.mediator.elmis.mock.MockDestination;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class DefaultOrchestratorTest {

    MediatorConfig config;
    /**
     * Represents the system actor.
     */
    protected static ActorSystem system;

    /**
     * Loads the mediator configuration.
     *
     * @param configPath The configuration path.
     * @return Returns the mediator configuration.
     */
    public static MediatorConfig loadConfig(String configPath) {
        MediatorConfig config = new MediatorConfig();


        try {
            if (configPath != null) {
                Properties props = new Properties();
                File conf = new File(configPath);
                InputStream in = FileUtils.openInputStream(conf);
                props.load(in);
                IOUtils.closeQuietly(in);

                config.setProperties(props);
            } else {
                config.setProperties("mediator.properties");
            }
        } catch (Exception e) {
            // TODO: handle this issue
        }

        config.setName(config.getProperty("mediator.name"));
        config.setServerHost(config.getProperty("mediator.host"));
        config.setServerPort(Integer.parseInt(config.getProperty("mediator.port")));
        config.setRootTimeout(Integer.parseInt(config.getProperty("mediator.timeout")));

        config.setCoreHost(config.getProperty("core.host"));
        config.setCoreAPIUsername(config.getProperty("core.api.user"));
        config.setCoreAPIPassword(config.getProperty("core.api.password"));

        config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        config.setHeartbeatsEnabled(true);

        return config;
    }

    /**
     * Adds dynamic configs to the mediator.
     *
     * @param mediatorConfig The mediator config.
     */
    public static void addDynamicConfigs(MediatorConfig mediatorConfig) {
        JSONObject gothomisConnectionProperties = new JSONObject();
        gothomisConnectionProperties.put("gothomisHost", "localhost");
        gothomisConnectionProperties.put("gothomisPort", "3000");
        gothomisConnectionProperties.put("gothomisPath", "/path");
        gothomisConnectionProperties.put("gothomisScheme", "http");
        gothomisConnectionProperties.put("gothomisUsername", "username");
        gothomisConnectionProperties.put("gothomisPassword", "password");


        JSONObject afyacareConnectionProperties = new JSONObject();
        afyacareConnectionProperties.put("afyacareHost", "localhost");
        afyacareConnectionProperties.put("afyacarePort", "3000");
        afyacareConnectionProperties.put("afyacarePath", "/path");
        afyacareConnectionProperties.put("afyacareScheme", "http");
        afyacareConnectionProperties.put("afyacareUsername", "username");
        afyacareConnectionProperties.put("afyacarePassword", "password");

        mediatorConfig.getDynamicConfig().put("gothomisConnectionProperties", gothomisConnectionProperties);
        mediatorConfig.getDynamicConfig().put("afyacareConnectionProperties", afyacareConnectionProperties);


    }

    /**
     * Runs cleanup after each test execution.
     */
    @After
    public void after() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Runs initialization before test execution.
     */
    @Before
    public void before() throws Exception {
        system = ActorSystem.create();
        config = loadConfig(null);

        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockDestination.class));
        TestingUtils.launchActors(system, config.getName(), toLaunch);
    }

    @Test
    public void testGotHOMISMediatorHTTPRequest() throws Exception {
        new JavaTestKit(system) {{
            MediatorConfig configuration = config;
            addDynamicConfigs(configuration);
            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("gothomis-requisition-status.json");

            final ActorRef defaultOrchestrator = system.actorOf(Props.create(DefaultOrchestrator.class, configuration));

            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/rnr-status",
                    IOUtils.toString(stream),
                    Collections.<String, String>singletonMap("Content-Type", "text/plain"),
                    Collections.<Pair<String, String>>emptyList()
            );

            defaultOrchestrator.tell(POST_Request, getRef());

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            boolean foundResponse = false;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    foundResponse = true;
                }
            }

            assertTrue("Must send FinishRequest", foundResponse);
        }};
    }

    @Test
    public void testAfyacareMediatorHTTPRequest() throws Exception {
        new JavaTestKit(system) {{
            MediatorConfig configuration = config;
            addDynamicConfigs(configuration);
            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("afyacare-requisition-status.json");

            final ActorRef defaultOrchestrator = system.actorOf(Props.create(DefaultOrchestrator.class, configuration));

            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/rnr-status",
                    IOUtils.toString(stream),
                    Collections.<String, String>singletonMap("Content-Type", "text/plain"),
                    Collections.<Pair<String, String>>emptyList()
            );

            defaultOrchestrator.tell(POST_Request, getRef());

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            boolean foundResponse = false;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    foundResponse = true;
                }
            }

            assertTrue("Must send FinishRequest", foundResponse);
        }};
    }
}
