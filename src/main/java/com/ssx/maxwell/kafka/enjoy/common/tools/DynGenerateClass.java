package com.ssx.maxwell.kafka.enjoy.common.tools;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 09:45
 * @description: no
 */
public class DynGenerateClass {

    public static void createClass() throws Exception {

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("com.ssx.maxwell.kafka.enjoy.service.TestService");
        //  add method
        CtMethod method = CtNewMethod.make("public String hello(String msg){return \"动态字节码hello输出->\"+msg;}", ctClass);
        ctClass.addMethod(method);
        //  add constructor
        CtConstructor constructor0 = CtNewConstructor.make("public TestService(){}", ctClass);
        ctClass.addConstructor(constructor0);

        Class cls = ctClass.toClass();
        Method method1 = cls.getMethod("hello", new Class[]{String.class});

        Constructor constructor = cls.getConstructor();

        System.out.println(method1.invoke(constructor.newInstance(), "hhhhhhhhhhhhhhhh"));

        ctClass.writeFile("./src/main/java");


//package com.ssx.maxwell.kafka.enjoy.service;
//
//         public class TestService {
//             public String hello(String var1) {
//                 return "动态字节码hello输出->" + var1;
//             }
//
//             public TestService() {
//             }
//         }

    }

    public static void createMClass() throws Exception {

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("com.ssx.maxwell.kafka.enjoy.service.TestService");
        ClassFile ccFile = ctClass.getClassFile();
        ConstPool constpool = ccFile.getConstPool();

        //interface
        ctClass.setInterfaces(new CtClass[]{classPool.makeInterface("java.io.Serializable"), classPool.makeInterface("java.lang.Cloneable")});


        //  类附上注解
        AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation data = new Annotation("lombok.Data", constpool);
        Annotation Accessors = new Annotation("lombok.experimental.Accessors", constpool);
        Accessors.addMemberValue("chain", new BooleanMemberValue(true, constpool));
        classAttr.addAnnotation(data);
        classAttr.addAnnotation(Accessors);
        ccFile.addAttribute(classAttr);


        //  add field
        CtField field = new CtField(CtClass.intType, "age", ctClass);
        field.setModifiers(AccessFlag.PUBLIC);
        ctClass.addField(field);
        FieldInfo fieldInfo = field.getFieldInfo();
        //  属性附上注解
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);

        CtMethod method = CtNewMethod.make("public String hello(String msg){return \"动态字节码hello输出->\"+msg;}", ctClass);
        ctClass.addMethod(method);



//         MethodInfo methodInfo = method.getMethodInfo();

        //方法附上注解
//         AnnotationsAttribute methodAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
//         Annotation annotation3 = new Annotation("org.springframework.web.bind.annotation.RequestMapping.RequestMapping", constpool);
//         annotation3.addMemberValue("value", new StringMemberValue("/register", constpool));
//         methodAttr.addAnnotation(annotation3);
//         methodInfo.addAttribute(methodAttr);
   /*
        //  add constructor
        CtConstructor constructor0 = CtNewConstructor.make("public MyClassDemo(){}", ctClass);
        CtConstructor constructor = CtNewConstructor.make("public MyClassDemo(int age){ this.age = age;}", ctClass);
        ctClass.addConstructor(constructor0);
        ctClass.addConstructor(constructor);



        Class tocls = ctClass.toClass();
        Method tom = tocls.getMethod("hello", new Class[]{String.class});

        Constructor toconstructor = tocls.getConstructor();

        System.out.println(tom.invoke(toconstructor.newInstance(), "hhhhhhhhhhhhhhhh"));*/


        ctClass.writeFile("./src/main/java");
    }

    public static void main(String[] args) throws Exception {
        DynGenerateClass.createClass();
    }
}
