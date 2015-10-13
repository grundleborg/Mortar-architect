package architect.autostack.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScopeSpec {

    private ClassName className;
    private ModuleSpec moduleSpec;
    private String daggerComponentBuilderDependencyMethodName;
    private Integer pathLayout;
    private TypeName daggerComponentBuilderDependencyTypeName;
    private TypeName daggerComponentTypeName;
    private TypeName parentComponentTypeName;
    private TypeName pathViewTypeName;
    private FieldSpec navigationResultSpec;
    private AnnotationSpec scopeAnnotationSpec;
    private AnnotationSpec componentAnnotationSpec;
    private List<FieldSpec> subscreenSpecs;
    private List<FieldSpec> navigationParamFieldSpecs;
    private List<List<ParameterSpec>> navigationParamConstructorSpecs;

    public ScopeSpec(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public String getDaggerComponentBuilderDependencyMethodName() {
        return daggerComponentBuilderDependencyMethodName;
    }

    public void setDaggerComponentBuilderDependencyMethodName(String daggerComponentBuilderDependencyMethodName) {
        this.daggerComponentBuilderDependencyMethodName = daggerComponentBuilderDependencyMethodName;
    }

    public Integer getPathLayout() {
        return pathLayout;
    }

    public void setPathLayout(Integer pathLayout) {
        this.pathLayout = pathLayout;
    }

    public TypeName getDaggerComponentBuilderDependencyTypeName() {
        return daggerComponentBuilderDependencyTypeName;
    }

    public void setDaggerComponentBuilderDependencyTypeName(TypeName daggerComponentBuilderDependencyTypeName) {
        this.daggerComponentBuilderDependencyTypeName = daggerComponentBuilderDependencyTypeName;
    }

    public void setModuleSpec(ModuleSpec moduleSpec) {
        this.moduleSpec = moduleSpec;
    }

    public TypeName getDaggerComponentTypeName() {
        return daggerComponentTypeName;
    }

    public void setDaggerComponentTypeName(TypeName daggerComponentTypeName) {
        this.daggerComponentTypeName = daggerComponentTypeName;
    }

    public TypeName getParentComponentTypeName() {
        return parentComponentTypeName;
    }

    public void setParentComponentTypeName(TypeName parentComponentTypeName) {
        this.parentComponentTypeName = parentComponentTypeName;
    }

    public TypeName getPathViewTypeName() {
        return pathViewTypeName;
    }

    public void setPathViewTypeName(TypeName pathViewTypeName) {
        this.pathViewTypeName = pathViewTypeName;
    }

    public FieldSpec getNavigationResultSpec() {
        return navigationResultSpec;
    }

    public void setNavigationResultSpec(FieldSpec navigationResultSpec) {
        this.navigationResultSpec = navigationResultSpec;
    }

    public AnnotationSpec getScopeAnnotationSpec() {
        return scopeAnnotationSpec;
    }

    public void setScopeAnnotationSpec(AnnotationSpec scopeAnnotationSpec) {
        this.scopeAnnotationSpec = scopeAnnotationSpec;
    }

    public AnnotationSpec getComponentAnnotationSpec() {
        return componentAnnotationSpec;
    }

    public void setComponentAnnotationSpec(AnnotationSpec componentAnnotationSpec) {
        this.componentAnnotationSpec = componentAnnotationSpec;
    }

    public List<FieldSpec> getSubscreenSpecs() {
        return subscreenSpecs;
    }

    public void setSubscreenSpecs(List<FieldSpec> subscreenSpecs) {
        this.subscreenSpecs = subscreenSpecs;
    }

    public List<FieldSpec> getNavigationParamFieldSpecs() {
        return navigationParamFieldSpecs;
    }

    public void setNavigationParamFieldSpecs(List<FieldSpec> navigationParamFieldSpecs) {
        this.navigationParamFieldSpecs = navigationParamFieldSpecs;
    }

    public List<List<ParameterSpec>> getNavigationParamConstructorSpecs() {
        return navigationParamConstructorSpecs;
    }

    public void setNavigationParamConstructorSpecs(List<List<ParameterSpec>> navigationParamConstructorSpecs) {
        this.navigationParamConstructorSpecs = navigationParamConstructorSpecs;
    }
}
