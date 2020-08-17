package com.fdzang.micro.common.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * @author tanghu
 * @Date: 2020/7/7 14:21
 */
public class LimitPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 为每个Example类添加limit和offset属性已经set、get方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        Field rows = new Field();
        rows.setName("rows");
        rows.setVisibility(JavaVisibility.PRIVATE);
        rows.setType(integerWrapper);
        topLevelClass.addField(rows);

        Method setRows = new Method();
        setRows.setVisibility(JavaVisibility.PUBLIC);
        setRows.setName("setRows");
        setRows.addParameter(new Parameter(integerWrapper, "rows"));
        setRows.addBodyLine("this.rows = rows;");
        topLevelClass.addMethod(setRows);

        Method getRows = new Method();
        getRows.setVisibility(JavaVisibility.PUBLIC);
        getRows.setReturnType(integerWrapper);
        getRows.setName("getRows");
        getRows.addBodyLine("return rows;");
        topLevelClass.addMethod(getRows);

        Field offset = new Field();
        offset.setName("offset");
        offset.setVisibility(JavaVisibility.PRIVATE);
        offset.setType(integerWrapper);
        topLevelClass.addField(offset);

        Method setOffset = new Method();
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.setName("setOffset");
        setOffset.addParameter(new Parameter(integerWrapper, "offset"));
        setOffset.addBodyLine("this.offset = offset;");
        topLevelClass.addMethod(setOffset);

        Method getOffset = new Method();
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(integerWrapper);
        getOffset.setName("getOffset");
        getOffset.addBodyLine("return offset;");
        topLevelClass.addMethod(getOffset);

        return true;
    }

    /**
     * 为Mapper.xml的selectByExample添加limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {

        XmlElement ifRowsNotNullElement = new XmlElement("if");
        ifRowsNotNullElement.addAttribute(new Attribute("test", "rows != null"));

        XmlElement ifOffsetNotNullElement = new XmlElement("if");
        ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${rows}"));
        ifRowsNotNullElement.addElement(ifOffsetNotNullElement);

        XmlElement ifOffsetNullElement = new XmlElement("if");
        ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        ifOffsetNullElement.addElement(new TextElement("limit ${rows}"));
        ifRowsNotNullElement.addElement(ifOffsetNullElement);

        element.addElement(ifRowsNotNullElement);

        return true;
    }
}
