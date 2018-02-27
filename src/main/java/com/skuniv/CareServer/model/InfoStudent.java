package com.skuniv.CareServer.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ±èÁÖÇö on 2017-11-12.
 */

public class InfoStudent {

    private int std_no;
    private String std_name;
    private int grade;
    private int parent_id;
    
    public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public int getStd_no() {
        return std_no;
    }

    public void setStd_no(int std_no) {
        this.std_no = std_no;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

	public String getStd_name() {
		return std_name;
	}

	public void setStd_name(String std_name) {
		this.std_name = std_name;
		try {
			this.std_name = URLEncoder.encode(this.std_name,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
