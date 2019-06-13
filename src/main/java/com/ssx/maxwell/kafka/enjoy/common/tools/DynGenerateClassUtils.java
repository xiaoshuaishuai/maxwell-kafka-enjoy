package com.ssx.maxwell.kafka.enjoy.common.tools;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 16:18
 * @description: no
 */
public class DynGenerateClassUtils {

    public static Class createBizClass(String packageName, String clsName, String dsKey) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(packageName + "." + clsName);
        ClassFile ccFile = ctClass.getClassFile();
        ConstPool constpool = ccFile.getConstPool();
        CtClass ctClassString = classPool.get("java.lang.String");
        CtClass jdbcTemplateClass = classPool.get("org.springframework.jdbc.core.JdbcTemplate");
        CtClass listClass = classPool.get("java.util.List");
        String fieldName = "jdbcTemplate";

        // set class annotation
        AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation ds = new Annotation("com.baomidou.dynamic.datasource.annotation.DS", constpool);
        ds.addMemberValue("value", new StringMemberValue(dsKey, constpool));
        classAttr.addAnnotation(ds);
        ccFile.addAttribute(classAttr);
        // set field
        CtField field = new CtField(jdbcTemplateClass, fieldName, ctClass);
        field.setModifiers(Modifier.PUBLIC);
        ctClass.addField(field);
        FieldInfo fieldInfo = field.getFieldInfo();
        // set field annotation
//        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
//        Annotation setter = new Annotation("lombok.Setter", constpool);
//        Annotation getter = new Annotation("lombok.Getter", constpool);
//        fieldAttr.addAnnotation(setter);
//        fieldAttr.addAnnotation(getter);
//        fieldInfo.addAttribute(fieldAttr);

        // add method queryForList(String sql)
        CtMethod ctMethod = new CtMethod(listClass, "queryForList", new CtClass[]{ctClassString}, ctClass);
        ctMethod.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        ctMethod.setBody("{return   " + fieldName + ".queryForList($1);}");
        ctClass.addMethod(ctMethod);

        CtMethod setJdbcTemplateMethod = new CtMethod(CtClass.voidType, "setJdbcTemplate", new CtClass[]{jdbcTemplateClass}, ctClass);
        setJdbcTemplateMethod.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        setJdbcTemplateMethod.setBody("{this." + fieldName + " = $1;}");
        ctClass.addMethod(setJdbcTemplateMethod);

        CtMethod getJdbcTemplateMethod = new CtMethod(jdbcTemplateClass, "getJdbcTemplate", null, ctClass);
        getJdbcTemplateMethod.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        getJdbcTemplateMethod.setBody("{return " + fieldName + ";}");
        ctClass.addMethod(getJdbcTemplateMethod);

        // add constructor
        CtConstructor constructor0 = CtNewConstructor.make("public " + clsName + "(){}", ctClass);
        ctClass.addConstructor(constructor0);

//        Class cls = ctClass.toClass();
//        Method method1 = cls.getMethod("hello", new Class[]{String.class});
//
//        Constructor constructor = cls.getConstructor();
//
//        System.out.println(method1.invoke(constructor.newInstance(), "hhhhhhhhhhhhhhhh"));
//        ctClass.debugWriteFile("./src/main/java");
        ctClass.writeFile("./src/main/java");
        return ctClass.toClass();
    }

    public static void main(String[] args) throws Exception {
        DynGenerateClassUtils.createBizClass("com.ssx.maxwell.kafka.enjoy.biz", "MaxwellDatabaseBizImpl", "master");
    }

}
