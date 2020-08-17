package com.fdzang.micro.common.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * @author tanghu
 * @Date: 2020/7/8 13:51
 */
public class LombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {

        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        // 添加domain的import
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.NoArgsConstructor");
        topLevelClass.addImportedType("lombok.AllArgsConstructor");
        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonProperty");
        topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");

        // 添加domain的注解
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@NoArgsConstructor");
        topLevelClass.addAnnotation("@AllArgsConstructor");

        // 添加@JsonProperty注解，给Date类型添加@JsonFormat注解
        List<Field> fields = topLevelClass.getFields();
        for (Field field : fields) {
            String fieldName = field.getName(); // java字段名
            field.addAnnotation("@JsonProperty(\"" + fieldName + "\")");

            if (field.getType().getFullyQualifiedName().equals("java.util.Date")) {
                field.addAnnotation("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")");
            }
        }

        // 设置 GENERATOR_DEFAULT_SERIAL_VERSION_UID
        generatorDefaultSerialVersionUID(topLevelClass);

        return true;
    }

    // 不生成getter
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        return false;
    }

    // 不生成setter
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        return false;
    }

    /**
     * 序列化
     *
     * @param topLevelClass
     */
    private void generatorDefaultSerialVersionUID(TopLevelClass topLevelClass) {
        FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
        topLevelClass.addSuperInterface(serializable);

        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.getFields().add(0, field);
    }
}