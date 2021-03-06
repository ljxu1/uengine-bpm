package org.uengine.processpublisher;

import org.uengine.kernel.Activity;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.Role;
import org.uengine.kernel.bpmn.SequenceFlow;

import java.util.Enumeration;
import java.util.Vector;

public class MigUtils {

    private static int MAX_TRACING_TAG = 0;

    private static boolean DRAW_LINE_PRE_ACTIVITY = false;

    private static boolean PARALLEL_STRUCTURE = false;

    private static Vector<Activity> PRE_ACITIVITIES;

    private static String PARENT_ROLE_NAME = "ROLE";

    private static double maxY = 0;

    public static double getYPosition(ProcessDefinition src, Activity curActivity){
        Activity tempActivity = curActivity;
        for(int i = 0 ; i < src.getWholeChildActivities().size() ; i++){
            if( i == 0 ){
                tempActivity.getElementView().setY(tempActivity.getElementView().getY() + MigDrawPositoin.getStartEventYPosition());
            }
            if(isDupYPosition(src, tempActivity)){
                tempActivity.getElementView().setY(tempActivity.getElementView().getY() + MigDrawPositoin.getActivityIntervalY() );
                maxY = tempActivity.getElementView().getY();
                System.out.println(tempActivity.getTracingTag() + " YYYYYYYYYYYYYYYYY " + tempActivity.getElementView().getY());
            }
        }

        return tempActivity.getElementView().getY();
    }

    //아래함수로는 위치조정후에는 겹치는게 없음.
/*    public static boolean isParallelStructure(ProcessDefinition src, Activity curActivity) {
        Activity tempActivity = curActivity;
        for(int i = 0 ; i < src.getWholeChildActivities().size() ; i++){
            if(isDupYPosition(src, tempActivity)){
               return true;
            }
        }
        return false;
    }*/

    private static boolean isDupYPosition(ProcessDefinition src, Activity curActivity) {
        Enumeration<Activity> enumeration = src.getWholeChildActivities().elements();
        while(enumeration.hasMoreElements()){
            Activity activity = (Activity)enumeration.nextElement();
            if(curActivity.getElementView().getX() == activity.getElementView().getX()
                    && curActivity.getElementView().getY() == activity.getElementView().getY()
                    && curActivity.getTracingTag().equals(activity.getTracingTag()) == false){

                System.out.println(curActivity.getTracingTag() + " vs " + activity.getTracingTag());
                return true;
            }
        }
        return false;
    }
    public static void setMaxTracingTag(ProcessDefinition src) {
        Enumeration enumeration = src.getWholeChildActivities().keys();

        int maxTrcTag = 0;
        while(enumeration.hasMoreElements()){
            int currTrcTag = cnvInt(enumeration.nextElement().toString());
            if( maxTrcTag < currTrcTag){
                maxTrcTag = currTrcTag;
            }
        }
        MAX_TRACING_TAG = maxTrcTag;
    }

    public static String getNewTracingTag(){
        return ++MAX_TRACING_TAG + "";
    }

    public static int cnvInt(String str){
        if(str == null || str.equals("")) return 0;
        return Integer.parseInt(str);
    }


    public static boolean isDrawLinePreActivity() {
        return DRAW_LINE_PRE_ACTIVITY;
    }

    public static void setDrawLinePreActivity(boolean drawLinePreActivity) {
        DRAW_LINE_PRE_ACTIVITY = drawLinePreActivity;
    }

    public static Vector<Activity> getPreAcitivities() {
        return PRE_ACITIVITIES;
    }

    public static void addPreActivities(Activity preActivity){
        if( PRE_ACITIVITIES == null){
            PRE_ACITIVITIES = new Vector<Activity>();
        }
        PRE_ACITIVITIES.add(preActivity);
    }

    //백터사이즈가 1인 경우에만 액티비티 리턴
    public static Activity getPreActivity(){
        if(PRE_ACITIVITIES.size() == 1){
            return PRE_ACITIVITIES.get(0);
        }
        return null;
    }

    public static Activity getPreActivity(String name){
        for(Activity activity : PRE_ACITIVITIES){
            if(activity.getName().equals(name)){
                return activity;
            }
        }
        return null;
    }

    public static void removeAllPreActivities(){
        PRE_ACITIVITIES.removeAllElements();
    }

    public static void removePreActivity(Activity preActivity){
        PRE_ACITIVITIES.remove(preActivity);
    }


    //Parallel Structure
    public static boolean isParallelStructure() {
        return PARALLEL_STRUCTURE;
    }

    public static void setParallelStructure(boolean parallelStructure) {
        PARALLEL_STRUCTURE = parallelStructure;
    }

    public static double getFirstRoleYPosition(ProcessDefinition processDefinition){
        double rv = MigDrawPositoin.getRoleXPosition();
        for(Role role : processDefinition.getRoles()){
            if(role.getElementView().getParent() != null) {
                if (rv > role.getElementView().getY()) {
                    rv = role.getElementView().getY();
                }
            }
        }
        return rv;
    }

    public static double getRoleTotalHeight(ProcessDefinition processDefinition){
        double rv = 0;
        for(Role role : processDefinition.getRoles()){
            if(role.getElementView().getParent() != null) {
                rv += role.getElementView().getHeight();
            }
        }
        return rv;
    }

    public static String getParentRoleName() {
        return PARENT_ROLE_NAME;
    }

    public static boolean isNotTransition(ProcessDefinition processDefinition, String trcTag) {
        for (SequenceFlow sFlow : processDefinition.getSequenceFlows()) {
            if (sFlow.getSourceRef().equals(trcTag)) {
                return false;
            }
        }
        return true;
    }
}
