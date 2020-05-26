package com.play.jvm;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * jvm 字节码增强
 * <p>
 * refs: https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html
 */
public class TestAgent {

    public static void agentmain(String args, Instrumentation inst) {
        //指定我们自己定义的Transformer，在其中利用Javassist做字节码替换
        inst.addTransformer(new TestTransformer(), true);
        try {
            //重定义类并载入新的字节码
            inst.retransformClasses(Base.class);
            System.out.println("Agent Load Done.");
        } catch (Exception e) {
            System.out.println("agent load failed!");
        }
        Arrays.asList("I","love", "you")
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));
    }

}

class Attacher {
    public static void main(String[] args) throws IOException, AgentLoadException, AgentInitializationException, AttachNotSupportedException {
        String pid = "9182";
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent("/home/zuo_ji/source-code/play-jvm/build/libs/play-jvm-1.0-SNAPSHOT.jar");
    }
}


class TestTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("Transforming " + className);
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get("com.play.jvm.Base");
            CtMethod m = cc.getDeclaredMethod("process");
            m.insertBefore("{ System.out.println(\"start\"); }");
            m.insertAfter("{ System.out.println(\"end\"); }");
            return cc.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


