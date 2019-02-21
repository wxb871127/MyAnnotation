package annotation.com.annotationlib;

import com.annotations.Template;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "com.annotations.State",
        "com.annotations.Template"
})
public class MyAnnotationProcess extends AbstractProcessor {

    private Filer mFiler;//文件辅助类
    private Elements mElementUtils;//元素相关的辅助类
    private Messager mMessager;//日志相关的辅助类

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Template.class);
        List<String> list = new ArrayList<>();
        for (Element element : elements)
        {
            if (element.getKind() != ElementKind.CLASS)
                throw new IllegalArgumentException("该注解不是类的注解");
//            ExecutableElement executableElement = (ExecutableElement) element;
            Template template = element.getAnnotation(Template.class);
            String tag = template.tag();
            list.add(tag);
        }



        MethodSpec main = null;
        if(list.size() > 0) {
            main = MethodSpec.methodBuilder(list.get(0))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!" + list.get(0))
                    .build();
        }
        TypeSpec typeSpec = null;
        if(main != null){
            typeSpec = TypeSpec.classBuilder("MyGeneratedClass")
                    .addModifiers(Modifier.PUBLIC).addMethod(main).build();
        }else {
            typeSpec = TypeSpec.classBuilder("MyGeneratedClass")
                    .addModifiers(Modifier.PUBLIC).build();
        }

        JavaFile javaFile = JavaFile.builder("com.annotationlib", typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
        Set<String> types = new LinkedHashSet<>();
        types.add(Override.class.getCanonicalName());
        return types;
    }
}
