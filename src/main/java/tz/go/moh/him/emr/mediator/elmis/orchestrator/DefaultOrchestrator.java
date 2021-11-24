package tz.go.moh.him.emr.mediator.elmis.orchestrator;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.emr.mediator.elmis.domain.RequisitionStatus;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

public class DefaultOrchestrator extends UntypedActor {
    /**
     * The serializer.
     */
    private static final JsonSerializer serializer = new JsonSerializer();

    /**
     * The mediator configuration.
     */
    private final MediatorConfig config;

    /**
     * Represents a mediator request.
     */
    protected MediatorHTTPRequest originalRequest;

    /**
     * The logger instance.
     */
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Initializes a new instance of the {@link DefaultOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DefaultOrchestrator(MediatorConfig config) {
        this.config = config;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;
            log.info("Received request: " + originalRequest.getHost() + " " + originalRequest.getMethod() + " " + originalRequest.getPath() + " " + originalRequest.getBody());

            RequisitionStatus requisitionStatus = null;
            try {
                requisitionStatus = serializer.deserialize(((MediatorHTTPRequest) msg).getBody(), RequisitionStatus.class);
            } catch (Exception e) {
                log.error(e.getMessage());

                FinishRequest finishRequest = new FinishRequest("Bad Request", "application/json", SC_BAD_REQUEST);
                (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
                return;
            }

            String host = null;
            int port = 0;
            String path = null;
            String scheme = null;
            String username = null;
            String password = null;

            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
            List<Pair<String, String>> parameters = new ArrayList<>();

            if (config.getDynamicConfig().isEmpty()) {
                log.debug("Dynamic config is empty, using config from mediator.properties");

                if (requisitionStatus.getSourceApplication().equalsIgnoreCase("GOTHOMIS")) {
                    host = config.getProperty("gothomis.host");
                    port = Integer.parseInt(config.getProperty("gothomis.port"));
                    path = config.getProperty("gothomis.path");

                    if (config.getProperty("destination.secure").equals("true")) {
                        scheme = "https";
                    } else {
                        scheme = "http";
                    }
                } else if (requisitionStatus.getSourceApplication().equalsIgnoreCase("AFYA_CARE")) {
                    host = config.getProperty("afyacare.host");
                    port = Integer.parseInt(config.getProperty("afyacare.port"));
                    path = config.getProperty("afyacare.path");

                    if (config.getProperty("afyacare.secure").equals("true")) {
                        scheme = "https";
                    } else {
                        scheme = "http";
                    }
                }
            } else {
                log.debug("Using dynamic config");

                if (requisitionStatus.getSourceApplication().equalsIgnoreCase("GOTHOMIS")) {
                    JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("gothomisConnectionProperties");
                    host = connectionProperties.getString("gothomisHost");
                    port = connectionProperties.getInt("gothomisPort");
                    path = connectionProperties.getString("gothomisPath");
                    scheme = connectionProperties.getString("gothomisScheme");

                    if (connectionProperties.has("gothomisUsername") && connectionProperties.has("gothomisPassword")) {
                        username = connectionProperties.getString("gothomisUsername");
                        password = connectionProperties.getString("gothomisPassword");
                    }
                } else if (requisitionStatus.getSourceApplication().equalsIgnoreCase("AFYA_CARE")) {
                    JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("afyacareConnectionProperties");
                    host = connectionProperties.getString("afyacareHost");
                    port = connectionProperties.getInt("afyacarePort");
                    path = connectionProperties.getString("afyacarePath");
                    scheme = connectionProperties.getString("afyacareScheme");

                    if (connectionProperties.has("afyacareUsername") && connectionProperties.has("afyacarePassword")) {
                        username = connectionProperties.getString("afyacareUsername");
                        password = connectionProperties.getString("afyacarePassword");
                    }
                }
                // if we have a username and a password
                // we want to add the username and password as the Basic Auth header in the HTTP request
                if (username != null && !"".equals(username) && password != null && !"".equals(password)) {
                    String auth = username + ":" + password;
                    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
                    String authHeader = "Basic " + new String(encodedAuth);
                    headers.put(HttpHeaders.AUTHORIZATION, authHeader);
                }
            }

            host = scheme + "://" + host + ":" + port + path;

            MediatorHTTPRequest request = new MediatorHTTPRequest(originalRequest.getRequestHandler(), getSelf(), host, "POST",
                    host, serializer.serializeToString(requisitionStatus), headers, parameters);

            ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
            httpConnector.tell(request, getSelf());
        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from target system");

            FinishRequest finishRequest = ((MediatorHTTPResponse) msg).toFinishRequest();
            (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
        } else {
            unhandled(msg);
        }
    }
}
