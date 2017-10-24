package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import accountdetail.bean.AccountDetailBean;
import employermanage.bean.EmployerManageBean;
import usermanage.bean.UserInfoBean;
import workermanage.bean.WorkerManageBean;

public class DataUtils {

    public static List<WorkerManageBean> getWorkerManageBeanList(String json) {
        List<WorkerManageBean> resultList = new ArrayList<>();
        try {
            JSONObject beanObj = new JSONObject(json);
            if (beanObj.optInt("code") == 200) {
                JSONArray dataArr = beanObj.optJSONArray("data");
                if (dataArr != null) {
                    for (int i = 0; i < dataArr.length(); i++) {
                        JSONObject obj = dataArr.optJSONObject(i);
                        if (obj != null) {
                            WorkerManageBean workerManageBean = new WorkerManageBean();
                            workerManageBean.setIcon(obj.optString("u_img"));
                            workerManageBean.setTitle(obj.optString("t_title"));
                            workerManageBean.setInfo(obj.optString("t_info"));
                            workerManageBean.setO_status(obj.optString("o_status"));
                            workerManageBean.setO_confirm(obj.optString("o_confirm"));
                            resultList.add(workerManageBean);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static List<EmployerManageBean> getEmployerManageBeanList(String json) {
        List<EmployerManageBean> resultList = new ArrayList<>();
        try {
            JSONObject beanObj = new JSONObject(json);
            if (beanObj.optInt("code") == 200) {
                JSONArray dataArr = beanObj.optJSONArray("data");
                if (dataArr != null) {
                    for (int i = 0; i < dataArr.length(); i++) {
                        JSONObject obj = dataArr.optJSONObject(i);
                        if (obj != null) {
                            EmployerManageBean employerManageBean = new EmployerManageBean();
                            employerManageBean.setIcon(obj.optString("u_img"));
                            employerManageBean.setTitle(obj.optString("t_title"));
                            employerManageBean.setInfo(obj.optString("t_info"));
                            employerManageBean.setStatus(obj.optString("t_status"));
                            resultList.add(employerManageBean);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static UserInfoBean getUserInfoBean(String json) {
        UserInfoBean userInfoBean = null;
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 1) {
                JSONObject objData = objBean.optJSONObject("data");
                if (objData != null) {
                    JSONObject obj = objData.optJSONObject("data");
                    if (obj != null) {
                        userInfoBean = new UserInfoBean();
                        userInfoBean.setU_id(obj.optString("u_id"));
                        userInfoBean.setU_name(obj.optString("u_name"));
                        userInfoBean.setU_mobile(obj.optString("u_mobile"));
                        userInfoBean.setU_phone(obj.optString("u_phone"));
                        userInfoBean.setU_fax(obj.optString("u_fax"));
                        userInfoBean.setU_sex(obj.optString("u_sex"));
                        userInfoBean.setU_in_time(obj.optString("u_in_time"));
                        userInfoBean.setU_online(obj.optString("u_online"));
                        userInfoBean.setU_status(obj.optString("u_status"));
                        userInfoBean.setU_type(obj.optString("u_type"));
                        userInfoBean.setU_task_status(obj.optString("u_task_status"));
                        userInfoBean.setU_skills(obj.optString("u_skills"));
                        userInfoBean.setU_start(obj.optString("u_start"));
                        userInfoBean.setU_credit(obj.optString("u_credit"));
                        userInfoBean.setU_top(obj.optString("u_top"));
                        userInfoBean.setU_recommend(obj.optString("u_recommend"));
                        userInfoBean.setU_jobs_num(obj.optString("u_jobs_num"));
                        userInfoBean.setU_worked_num(obj.optString("u_worked_num"));
                        userInfoBean.setU_high_opinions(obj.optString("u_high_opinions"));
                        userInfoBean.setU_low_opinions(obj.optString("u_low_opinions"));
                        userInfoBean.setU_middle_opinions(obj.optString("u_middle_opinions"));
                        userInfoBean.setU_dissensions(obj.optString("u_dissensions"));
                        userInfoBean.setU_true_name(obj.optString("u_true_name"));
                        userInfoBean.setU_idcard(obj.optString("u_idcard"));
                        userInfoBean.setU_info(obj.optString("u_info"));
                        JSONObject objArea = obj.optJSONObject("area");
                        if (objArea != null) {
                            userInfoBean.setArea_uei_province(objArea.optString("uei_province"));
                            userInfoBean.setArea_uei_city(objArea.optString("uei_city"));
                            userInfoBean.setArea_uei_area(objArea.optString("uei_area"));
                            userInfoBean.setArea_uei_address(objArea.optString("uei_address"));
                            userInfoBean.setArea_user_area_name(objArea.optString("user_area_name"));
                        }
                        userInfoBean.setU_img(obj.optString("u_img"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInfoBean;
    }

    public static List<AccountDetailBean> getAccountDetailBeanList(String json) {
        List<AccountDetailBean> resultList = new ArrayList<>();
        try {
            JSONObject beanObj = new JSONObject(json);
            if (beanObj.optInt("code") == 1) {
                JSONObject dataObj = beanObj.optJSONObject("data");
                if (dataObj != null) {
                    JSONArray dataArr = dataObj.optJSONArray("data");
                    if (dataArr != null) {
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject obj = dataArr.optJSONObject(i);
                            if (obj != null) {
                                AccountDetailBean adb = new AccountDetailBean();
                                adb.setTitle(obj.optString("type"));
                                adb.setTime(obj.optString("time"));
                                adb.setBalance(obj.optString("balances"));
                                adb.setDes(obj.optString("amount"));
                                resultList.add(adb);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
