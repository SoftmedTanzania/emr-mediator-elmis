package tz.go.moh.him.emr.mediator.elmis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequisitionStatus {
    /**
     * The source application. either AfyaCare or GoTHOMIS.
     */
    @SerializedName("sourceApplication")
    @JsonProperty("sourceApplication")
    private String sourceApplication;

    /**
     * The program code.
     */
    @SerializedName("program")
    @JsonProperty("program")
    private String program;

    /**
     * The HFR code of the facility.
     */
    @SerializedName("hfrId")
    @JsonProperty("hfrId")
    private String hfrId;

    /**
     * The flag whether the rnr was an emergency.
     */
    @SerializedName("isEmergency")
    @JsonProperty("isEmergency")
    private boolean isEmergency;

    /**
     * The Report and Requisition ID
     */
    @SerializedName("rnrId")
    @JsonProperty("rnrId")
    private String rnrId;

    /**
     * The Reporting period
     */
    @SerializedName("period")
    @JsonProperty("period")
    private String period;

    /**
     * The R&R status
     */
    @SerializedName("status")
    @JsonProperty("status")
    private String status;

    /**
     * The R&R status description
     */
    @SerializedName("description")
    @JsonProperty("description")
    private String description;

    /**
     * The Date Time
     */
    @SerializedName("dateTime")
    @JsonProperty("dateTime")
    private String dateTime;

    /**
     * The actor's name who acted on the R&R
     */
    private String actedBy;

    /**
     * The R&R status supply chain process level
     */
    private String level;

    /**
     * The R&R rejection object
     */
    private Rejection rejection;

    public String getSourceApplication() {
        return sourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        this.sourceApplication = sourceApplication;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getHfrId() {
        return hfrId;
    }

    public void setHfrId(String hfrId) {
        this.hfrId = hfrId;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public String getRnrId() {
        return rnrId;
    }

    public void setRnrId(String rnrId) {
        this.rnrId = rnrId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getActedBy() {
        return actedBy;
    }

    public void setActedBy(String actedBy) {
        this.actedBy = actedBy;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    public static class Rejection {
        /**
         * The R&R rejection reasons
         */
        private List<RejectionReasons> reasons;

        /**
         * The R&R rejection comments
         */
        private String comment;

        public List<RejectionReasons> getReasons() {
            return reasons;
        }

        public void setReasons(List<RejectionReasons> reasons) {
            this.reasons = reasons;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class RejectionReasons {
        /**
         * The R&R rejection reason code
         */
        private String code;

        /**
         * The rejection reason description
         */
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
