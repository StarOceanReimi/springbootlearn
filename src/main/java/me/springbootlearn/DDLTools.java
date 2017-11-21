package me.springbootlearn;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.TargetTypeHelper;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputNonExistentImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

/**
 * Created by reimi on 11/18/17.
 */
public class DDLTools {

    public static Set<Class<?>> scanClassesByAnnotations(String packageName, Class<? extends Annotation>... annoFilters) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        Arrays.stream(annoFilters)
                .map(anno -> new AnnotationTypeFilter(anno))
                .forEach(filter -> provider.addIncludeFilter(filter));
        Function<BeanDefinition, Class<?>> mapper = (beanDef)->{
            try {
                return Class.forName(beanDef.getBeanClassName());
            } catch (ClassNotFoundException e) {
                return null;
            }
        };
        return provider.findCandidateComponents(packageName).stream()
                        .map(mapper)
                        .filter(cls -> cls != null)
                        .collect(toSet());
    }

    public static void main(String[] args) throws IOException {
        Properties hbnProp = new Properties();
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        InputStream configInput = contextLoader.getResourceAsStream("META-INF/hibernate.properties");
        hbnProp.load(configInput);
        String key = "hibernate.connection.password";
        String cipherPassword = hbnProp.get(key).toString();
        String dbAccessPass = System.getProperty("dbaccess_pass");
        char[] pass = null;
        if(dbAccessPass == null)
            pass = CipherTools.showPasswordInputPane();
        else
            pass = dbAccessPass.toCharArray();
        String realPassword = CipherTools.decryptByPassword(cipherPassword, pass);
        hbnProp.setProperty(key, realPassword);
        MetadataSources sources = new MetadataSources(
                new StandardServiceRegistryBuilder()
                    .applySettings(hbnProp)
                    .build());
        Set<Class<?>> classes = scanClassesByAnnotations("me.springbootlearn.entity", Entity.class);
        for(Class<?> clazz : classes) sources.addAnnotatedClass(clazz);
        Metadata meta = sources.buildMetadata();
        SchemaExport export = new SchemaExport();
        export.setHaltOnError(true)
              .setFormat(false);
        export.create(TargetTypeHelper.parseCommandLineOptions("database,stdout"), meta);
        ((StandardServiceRegistryImpl)sources.getServiceRegistry()).destroy();
    }
}
