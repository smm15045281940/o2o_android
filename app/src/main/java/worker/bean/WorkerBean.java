package worker.bean;


import java.io.Serializable;

public class WorkerBean implements Serializable{

    private String u_id;
    private String u_name;
    private String u_skills;
    private String uei_info;
    private String u_task_status;
    private String u_true_name;
    private String ucp_posit_x;
    private String ucp_posit_y;
    private String u_img;

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_skills() {
        return u_skills;
    }

    public void setU_skills(String u_skills) {
        this.u_skills = u_skills;
    }

    public String getUei_info() {
        return uei_info;
    }

    public void setUei_info(String uei_info) {
        this.uei_info = uei_info;
    }

    public String getU_task_status() {
        return u_task_status;
    }

    public void setU_task_status(String u_task_status) {
        this.u_task_status = u_task_status;
    }

    public String getU_true_name() {
        return u_true_name;
    }

    public void setU_true_name(String u_true_name) {
        this.u_true_name = u_true_name;
    }

    public String getUcp_posit_x() {
        return ucp_posit_x;
    }

    public void setUcp_posit_x(String ucp_posit_x) {
        this.ucp_posit_x = ucp_posit_x;
    }

    public String getUcp_posit_y() {
        return ucp_posit_y;
    }

    public void setUcp_posit_y(String ucp_posit_y) {
        this.ucp_posit_y = ucp_posit_y;
    }

    public String getU_img() {
        return u_img;
    }

    public void setU_img(String u_img) {
        this.u_img = u_img;
    }

    @Override
    public String toString() {
        return "WorkerBean{" +
                "u_id='" + u_id + '\'' +
                ", u_name='" + u_name + '\'' +
                ", u_skills='" + u_skills + '\'' +
                ", uei_info='" + uei_info + '\'' +
                ", u_task_status='" + u_task_status + '\'' +
                ", u_true_name='" + u_true_name + '\'' +
                ", ucp_posit_x='" + ucp_posit_x + '\'' +
                ", ucp_posit_y='" + ucp_posit_y + '\'' +
                ", u_img='" + u_img + '\'' +
                '}';
    }
}
