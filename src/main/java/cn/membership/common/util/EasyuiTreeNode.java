package cn.membership.common.util;

import java.awt.Menu;
import java.util.ArrayList;
import java.util.List;

public class EasyuiTreeNode {
	
	private Long id;
	private String text;
	private String iconCls;
	private Long parentId;
	private List<EasyuiTreeNode> children = new ArrayList<EasyuiTreeNode>();
	private boolean checked;
	
	private Menu attributes;
	
	public void addChild(EasyuiTreeNode node) {
		if (! this.children.contains(node)) {
			this.children.add(node);
		}
	}
	
	public boolean isLeaf() {
		return this.children.isEmpty();
	}
	
	public Menu getAttributes() {
		return attributes;
	}

	public void setAttributes(Menu attributes) {
		this.attributes = attributes;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public List<EasyuiTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<EasyuiTreeNode> children) {
		this.children = children;
	}
	
	
	
}
