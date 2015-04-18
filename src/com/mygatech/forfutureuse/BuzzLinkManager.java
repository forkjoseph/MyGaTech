package com.mygatech.forfutureuse;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

public class BuzzLinkManager {
	//		Map<step, Map<under, Map<title, link>>>
	//ex)	Map<0, Map<ROOT,Map<"STUDENT_SERVICE",STUDENT_SERVICE>>>
	//ex) 	Map<2, Map<REGISTRATION, Map<"LOOK_UP", LOOK_UP>>>
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, HashMap<String, HashMap<String,String>>> list = new HashMap<Integer, HashMap<String, HashMap<String,String>>>();
	
	//Root - 0 
	public final String STUDENT_SERVICE = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu";
	public final String PERSONAL_INFO = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu";
	public final String CAMPUS_SERVICE = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=wmenu.P_MainCSMnu";
	
	//Student service -1
	public final String REGISTRATION = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=bmenu.P_RegMnu";
	public final String STUDENT_RECORD = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=bmenu.P_AdminMnu";
	public final String FINANCIAL_AID = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=wmenu.P_FAApplStuMnu";
	public final String STUDENT_ACCOUNT = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=wmenu.P_FAApplStuMnu";
	
	//Personal info - 1
	public final String CHANGE_PIM = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_ChangePin";
	public final String CHANGE_QUESTION = "https://oscar.gatech.edu/pls/bprod/twbkwbis.P_SecurityQuestion";
	public final String UPDATE_INFO = "https://oscar.gatech.edu/pls/bprod/bwgkogad.P_SelectAtypUpdate";
	public final String VIEW_EMAIL = "https://oscar.gatech.edu/pls/bprod/bwgkogad.P_SelectEmalView";
	
	//Registration -2 
	public final String ADD_OR_DROP = "https://oscar.gatech.edu/pls/bprod/bwskfreg.P_AltPin";
	public final String LOOK_UP = "https://oscar.gatech.edu/pls/bprod/bwskfcls.p_sel_crse_search";
	public final String CHANGE_OPTION = "https://oscar.gatech.edu/pls/bprod/bwskfreg.P_ChangeCrseOpt";
	public final String WEEK_AT_GALANCE = "https://oscar.gatech.edu/pls/bprod/bwskfshd.P_CrseSchd";
	public final String DETAIL_CLASS = "https://oscar.gatech.edu/pls/bprod/bwskfshd.P_CrseSchdDetl";
	public final String REGIS_STATUS = "https://oscar.gatech.edu/pls/bprod/bwskrsta.P_RegsStatusDisp";
	public final String OVERRIDE_RST = "https://oscar.gatech.edu/pls/bprod/wwptovr.P_PtOvrRqst_SelSubj";
	public final String OVERRIDE_STATUS = "https://oscar.gatech.edu/pls/bprod/wwpstat.P_PtOvrStat";
	public final String TEXT_BOOKS = "https://oscar.gatech.edu/pls/bprod/wwsktexb.p_crseschdtext";
	
	//Student record - 2
	public final String GRADUATION_TERM = "https://oscar.gatech.edu/pls/bprod/wwskagdt.anticipated_grad_term";
	public final String VIEW_HOLDS = "https://oscar.gatech.edu/pls/bprod/bwskoacc.P_ViewHold";
 	public final String PROGRESS_REPORT = "https://oscar.gatech.edu/pls/bprod/bwskmgrd.p_write_term_selection";
	public final String FINAL_GRADES = "https://oscar.gatech.edu/pls/bprod/bwskogrd.P_ViewTermGrde";
	public final String UNOFFICIAL_TRANSCRIPT = "https://oscar.gatech.edu/pls/bprod/bwskotrn.P_ViewTermTran";
	public final String VERIFICATION_REQUEST = "https://oscar.gatech.edu/pls/bprod/bwskrqst.p_disp_term_type";
	public final String VIEW_STATUS_VERIFICATION = "https://oscar.gatech.edu/pls/bprod/bwskrqst.p_disp_request_dates";
	public final String ONLINE_DEGREE_VERIFICATION = "https://oscar.gatech.edu/pls/bprod/wwskdegv.P_DegVerLaunchPage";
	public final String ACCOUNT_SUMMARY_BY_TERM = "https://oscar.gatech.edu/pls/bprod/bwskoacc.P_ViewAcct";
	public final String ACCOUNT_SUMMARY = "https://oscar.gatech.edu/pls/bprod/bwskoacc.P_ViewAcctTotal";
	public final String DEGREE_WORK = "https://degreeaudit.gatech.edu/IRISLink.cgi";
	public final String APPLY_TO_GRADUATE = "https://oscar.gatech.edu/pls/bprod/bwskgrad.p_disp_grad_term";
	public final String VIEW_APPLICATION_TO_GRADUATE = "https://oscar.gatech.edu/pls/bprod/bwskgrad.p_view_gradapp";
	public final String DEGREE_CANDIDATE = "http://www.registrar.gatech.edu/students/deginfo/oag.php";
	public final String PO_BOX = "https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox";
	public final String CHANGE_PROGRAM = "https://oscar.gatech.edu/pls/bprod/wwskconc.P_SelectConc";
	public final String TRANSFER_CREDIT = "https://oscar.gatech.edu/pls/bprod/wwsktrns.P_DispCurrent";
	public final String TRANSFER_ARTIC = "https://oscar.gatech.edu/pls/bprod/wwsktrna.P_find_location";
		
	public BuzzLinkManager(){
		list.put(0, zeroMap());
		list.put(1, oneMap());
		list.put(2, twoMap());
		//Log.e("check", Integer.toString(list.get(0).size()));
	}
	
	public HashMap<String, String> getMap(int step, String prev){
		//Log.e("check", Integer.toString(list.get(step).get(prev).size()));
		return (list.get(step).get(prev));
	}
	
	public HashMap<String, HashMap<String, String>> zeroMap(){
		HashMap<String,String> listOf = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> temp = new HashMap<String, HashMap<String, String>> ();
		listOf.put("STUDENT_SERVICE", STUDENT_SERVICE);
		listOf.put("PERSONAL_INFO", PERSONAL_INFO);
		listOf.put("CAMPUS_SERVICE", CAMPUS_SERVICE);
		temp.put("ROOT", listOf);
		return temp;
	}
	
	public HashMap<String, HashMap<String, String>> oneMap(){
		HashMap<String,String> listOf1 = new HashMap<String, String>();
		HashMap<String,String> listOf2 = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> temp = new HashMap<String, HashMap<String, String>> ();
		listOf1.put("STUDENT_ACCOUNT", STUDENT_ACCOUNT);
		listOf1.put("FINANCIAL_AID", FINANCIAL_AID);
		listOf1.put("STUDENT_RECORD", STUDENT_RECORD);
		listOf1.put("REGISTRATION", REGISTRATION);
		temp.put("STUDENT_SERVICE", listOf1);
		listOf2.put("VIEW_EMAIL", VIEW_EMAIL);
		listOf2.put("UPDATE_INFO", UPDATE_INFO);
		listOf2.put("CHANGE_QUESTION",CHANGE_QUESTION);
		listOf2.put("CHANGE_PIM", CHANGE_PIM);
		temp.put("PERSONAL_INFO", listOf2);
		return temp;
	}
	
	public HashMap<String, HashMap<String, String>> twoMap(){
		HashMap<String,String> listOf1 = new HashMap<String, String>();
		HashMap<String,String> listOf2 = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> temp = new HashMap<String, HashMap<String, String>> ();
		listOf1.put("ADD_OR_DROP", ADD_OR_DROP);
		listOf1.put("LOOK_UP", LOOK_UP);
		listOf1.put("CHANGE_OPTION", CHANGE_OPTION);
		listOf1.put("WEEK_AT_GALANCE", WEEK_AT_GALANCE);
		listOf1.put("DETAIL_CLASS", DETAIL_CLASS);
		listOf1.put("REGIS_STATUS", REGIS_STATUS);
		listOf1.put("OVERRIDE_RST", OVERRIDE_RST);
		listOf1.put("OVERRIDE_STATUS", OVERRIDE_STATUS);
		temp.put("REGISTRATION", listOf1);
		listOf2.put("GRADUATION_TERM", GRADUATION_TERM);
		listOf2.put("VIEW_HOLDS", VIEW_HOLDS);
		listOf2.put("PROGRESS_REPORT",PROGRESS_REPORT);
		listOf2.put("PROGRESS_REPORT",PROGRESS_REPORT);
		listOf2.put("UNOFFICIAL_TRANSCRIPT",UNOFFICIAL_TRANSCRIPT);
		listOf2.put("VERIFICATION_REQUEST",VERIFICATION_REQUEST);
		listOf2.put("VIEW_STATUS_VERIFICATION",VIEW_STATUS_VERIFICATION);
		listOf2.put("ONLINE_DEGREE_VERIFICATION",ONLINE_DEGREE_VERIFICATION);
		listOf2.put("ACCOUNT_SUMMARY_BY_TERM",ACCOUNT_SUMMARY_BY_TERM);
		listOf2.put("ACCOUNT_SUMMARY",ACCOUNT_SUMMARY);
		listOf2.put("DEGREE_WORK",DEGREE_WORK);
		listOf2.put("APPLY_TO_GRADUATE",APPLY_TO_GRADUATE);
		listOf2.put("VIEW_APPLICATION_TO_GRADUATE",VIEW_APPLICATION_TO_GRADUATE);
		listOf2.put("DEGREE_CANDIDATE",DEGREE_CANDIDATE);
		listOf2.put("PO_BOX",PO_BOX);
		listOf2.put("CHANGE_PROGRAM",CHANGE_PROGRAM);
		listOf2.put("TRANSFER_CREDIT",TRANSFER_CREDIT);
		listOf2.put("TRANSFER_ARTIC",TRANSFER_ARTIC);
		temp.put("STUDENT_RECORD", listOf2);
		return temp;
	}
}
