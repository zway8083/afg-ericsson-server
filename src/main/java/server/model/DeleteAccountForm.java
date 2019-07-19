package server.model;


public class DeleteAccountForm {


    public DeleteAccountForm(){
    }


    public DeleteAccountForm(Long subjectId,Long userId,Long deviceId){
        this.userId=userId;
        this.subjectId=subjectId;
        this.deviceId=deviceId;
    }


    private Long subjectId;
    private Long userId;
    private Long deviceId;

    public Long getSubjectId(){return subjectId;}

    public void setSubjectId(Long subjectId){this.subjectId = subjectId;}

    public Long getUserId(){return userId;}

    public void setUserId(Long userId){this.userId = userId;}

    public Long getDeviceId(){return deviceId;}

    public void setDeviceId(Long deviceId){this.deviceId = deviceId;}


}
