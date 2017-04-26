package org.mybatis.generator.plugins.myplugin;

import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.api.dom.xml.Element;

public class MysqlSplitingTablePlugin extends PluginAdapterEnhance {

	private Logger log = Logger.getLogger(this.getClass());

	private String tableName = "tableName";

	public MysqlSplitingTablePlugin() {
		log.debug("initialized");
	}

	/**
	 * 在Exmaple类中添加tableName字段
	 */
	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {

		Field tableName = new Field(this.tableName,
				PrimitiveTypeWrapper.getStringInstance());
		// 默认设置为当前的table名字
		tableName.setInitializationString("\""
				+ introspectedTable.getTableConfiguration().getTableName()
				+ "\"");
		tableName.setVisibility(JavaVisibility.PRIVATE);
		addField(topLevelClass, introspectedTable, tableName);
		addTableNameHelperMethod(topLevelClass, introspectedTable);
		return super.modelExampleClassGenerated(topLevelClass,
				introspectedTable);
	}

	/**
	 * 在object类中添加tableName字段
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {

		Field tableName = new Field(this.tableName,
				PrimitiveTypeWrapper.getStringInstance());
		// 默认设置为当前的table名字
		tableName.setInitializationString("\""
				+ introspectedTable.getTableConfiguration().getTableName()
				+ "\"");
		tableName.setVisibility(JavaVisibility.PRIVATE);
		addField(topLevelClass, introspectedTable, tableName);
		addTableNameHelperMethod(topLevelClass, introspectedTable);
		return super.modelBaseRecordClassGenerated(topLevelClass,
				introspectedTable);
	}

	/**
	 * 这三个函数在分表中需要用其他函数替换，以为分表需要传入table名字，但是count函数需要处理*/
	@Override
	public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		// no way to get the tablename so let it stay unchanged
		return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable){
		resetDeleteXmlElementTableName(element);
		return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapCountByExampleElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable){
		resetCountByExample(element);
		return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		// no way to get the tablename so let it stay unchanged
		return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
	}

	/**
	 * 在xml的SelectByExample的SQL语句添加limit
	 */
	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetSelectXmlElementTableName(element);
		return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,
				introspectedTable);
	}

	@Override
	public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetSelectXmlElementTableName(element);
		return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element,
				introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByExampleSelectiveElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableName(element);
		return super.sqlMapUpdateByExampleSelectiveElementGenerated(element,
				introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableName(element);
		return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
		
	}

	@Override
	public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableName(element);
		return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
				introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableNameNotMapType(element);
		return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableNameNotMapType(element);
		return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
				element, introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		resetUpdateXmlElementTableNameNotMapType(element);
		return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		resetInsertXmlElementTableName(element);
		return super.sqlMapInsertElementGenerated(element, introspectedTable);
	}

	@Override
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		resetInsertXmlElementTableName(element);
		return super.sqlMapInsertSelectiveElementGenerated(element,
				introspectedTable);
	}
	
	private void resetSelectXmlElementTableName(XmlElement element) {
		List<Element> elements = element.getElements();
		TextElement subSentence = new TextElement("from ${" + tableName + "}");
		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			String content = e.getFormattedContent(0);
			if (content.startsWith("from ")) {
				String[] data = content.split(" ");
				data[1] = "${" + tableName + "}";
				elements.set(i, subSentence);
			}
		}
	}

	private void resetInsertXmlElementTableName(XmlElement element) {
		List<Element> elements = element.getElements();
		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			String content = e.getFormattedContent(0);
			log.info("content: " + content);
			if (content.startsWith("insert into")) {
				String[] data = content.split(" ");
				data[2] = "${" + tableName + "}";
				TextElement subSentence = new TextElement(
						MysqlSplitingTablePlugin.join(" ", data));
				elements.set(i, subSentence);
			}
		}
	}

	private void resetDeleteXmlElementTableName(XmlElement element) {
		List<Element> elements = element.getElements();
		String content = elements.get(0).getFormattedContent(0);
		String[] data = content.split(" ");
		data[2] = "${" + tableName + "}";
		TextElement subSentence = new TextElement(
				MysqlSplitingTablePlugin.join(" ", data));
		elements.set(0, subSentence);
	}

	private void resetUpdateXmlElementTableName(XmlElement element) {
		List<Element> elements = element.getElements();
		TextElement subSentence = new TextElement("update ${record."
				+ tableName + "}");
		elements.set(0, subSentence);
	}

	private void resetUpdateXmlElementTableNameNotMapType(XmlElement element) {
		List<Element> elements = element.getElements();
		TextElement subSentence = new TextElement("update ${" + tableName + "}");
		elements.set(0, subSentence);
	}
	
	private void resetCountByExample(XmlElement element) {
		List<Element> elements = element.getElements();
		String content = elements.get(0).getFormattedContent(0);
		String[] data = content.split(" ");
		data[3] = "${" + tableName + "}";
		TextElement subSentence = new TextElement(
				MysqlSplitingTablePlugin.join(" ", data));
		elements.set(0, subSentence);
	}

	private void addTableNameHelperMethod(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// generate helper set method
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Method method = new Method();
		String fieldName = this.tableName;
		String methodName =
				"setActual" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		method.setVisibility(JavaVisibility.PUBLIC);	
		method.setName(methodName);
		method.addParameter(new Parameter(PrimitiveTypeWrapper.getStringInstance(), "batchCode"));
		method.addBodyLine("this." + fieldName + " += \"_\" + batchCode;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
	}

	public static String join(String join, String[] strAry) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strAry.length; i++) {
			if (i == (strAry.length - 1)) {
				sb.append(strAry[i]);
			} else {
				sb.append(strAry[i]).append(join);
			}
		}
		return new String(sb);
	}
}
