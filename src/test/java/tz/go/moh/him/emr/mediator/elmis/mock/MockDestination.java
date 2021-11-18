package tz.go.moh.him.emr.mediator.elmis.mock;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import tz.go.moh.him.emr.mediator.elmis.domain.RequisitionStatus;
import tz.go.moh.him.emr.mediator.elmis.orchestrator.DefaultOrchestratorTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a mock class for the Destination system.
 */
public class MockDestination extends MockHTTPConnector {
    /**
     * Gets the response.
     *
     * @return Returns the response.
     */
    @Override
    public String getResponse() {
        return "{\n" +
                "   \"code\": 200,\n" +
                "   \"success\": true,\n" +
                "   \"message\": \"success\",\n" +
                "   \"data\": {\n" +
                "       \"created\": 1,\n" +
                "       \"updated\": 0,\n" +
                "       \"error\": 0,\n" +
                "       \"response\": [\n" +
                "           {\n" +
                "               \"message\": \"Created\",\n" +
                "               \"uuid\": \"3d378375-6a0c-4974-b737-4160c293774d\",\n" +
                "               \"code\": 1,\n" +
                "               \"errors\": null\n" +
                "           }\n" +
                "       ]\n" +
                "   }\n" +
                "}\n";
    }

    /**
     * Gets the status code.
     *
     * @return Returns the status code.
     */
    @Override
    public Integer getStatus() {
        return 200;
    }

    /**
     * Gets the HTTP headers.
     *
     * @return Returns the HTTP headers.
     */
    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }


    /**
     * Handles the message.
     *
     * @param msg The message.
     */
    @Override
    public void executeOnReceive(MediatorHTTPRequest msg) {
        RequisitionStatus requisitionStatus = new Gson().fromJson(msg.getBody(), RequisitionStatus.class);
        RequisitionStatus actual = null;
        if (requisitionStatus.getSourceApplication().equalsIgnoreCase("GOTHOMIS")) {
            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("gothomis-requisition-status.json");
            try {
                actual = new Gson().fromJson(IOUtils.toString(stream), RequisitionStatus.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requisitionStatus.getSourceApplication().equalsIgnoreCase("AFYA_CARE")) {
            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("afyacare-requisition-status.json");
            try {
                actual = new Gson().fromJson(IOUtils.toString(stream), RequisitionStatus.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Assert.assertEquals(requisitionStatus.getSourceApplication(), actual.getSourceApplication());
        Assert.assertEquals(requisitionStatus.getStatus(), actual.getStatus());
        Assert.assertEquals(requisitionStatus.getActedBy(), actual.getActedBy());
        Assert.assertEquals(requisitionStatus.getDescription(), actual.getDescription());
        Assert.assertEquals(requisitionStatus.getDateTime(), actual.getDateTime());
        Assert.assertEquals(requisitionStatus.getHfrId(), actual.getHfrId());
        Assert.assertEquals(requisitionStatus.getLevel(), actual.getLevel());
        Assert.assertEquals(requisitionStatus.getPeriod(), actual.getPeriod());
        Assert.assertEquals(requisitionStatus.getProgram(), actual.getProgram());
        Assert.assertEquals(requisitionStatus.getRnrId(), actual.getRnrId());

    }
}