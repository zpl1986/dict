/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.vo;

/**
 * 
 *
 * @author
 * @version 0.0.1
 * @since
 */
public class Word {
	private String id;
	private String content;
	private String youdao;

	private Boolean cz;//初中
	private Boolean gz;//高中
	private Boolean cet4;
	private Boolean cet6;
	private Boolean ky;
	private Boolean tem4;
	private Boolean tem8;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public Boolean getCz() {
		return cz;
	}

	public void setCz(Boolean cz) {
		this.cz = cz;
	}

	public Boolean getGz() {
		return gz;
	}

	public void setGz(Boolean gz) {
		this.gz = gz;
	}

	public Boolean getCet4() {
		return cet4;
	}

	public void setCet4(Boolean cet4) {
		this.cet4 = cet4;
	}

	public Boolean getCet6() {
		return cet6;
	}

	public void setCet6(Boolean cet6) {
		this.cet6 = cet6;
	}

	public Boolean getKy() {
		return ky;
	}

	public void setKy(Boolean ky) {
		this.ky = ky;
	}

	public Boolean getTem4() {
		return tem4;
	}

	public void setTem4(Boolean tem4) {
		this.tem4 = tem4;
	}

	public Boolean getTem8() {
		return tem8;
	}

	public void setTem8(Boolean tem8) {
		this.tem8 = tem8;
	}

	public String getYoudao() {
		return youdao;
	}

	public void setYoudao(String youdao) {
		this.youdao = youdao;
	}
}