package fr.octopiastudios.api.utils.despical.jvm.classes;

import fr.octopiastudios.api.utils.ReflectionUtils;
import fr.octopiastudios.api.utils.despical.Handle;
import fr.octopiastudios.api.utils.despical.jvm.ConstructorMemberHandle;
import fr.octopiastudios.api.utils.despical.jvm.FieldMemberHandle;
import fr.octopiastudios.api.utils.despical.jvm.FieldMemberHandleJava8;
import fr.octopiastudios.api.utils.despical.jvm.MethodMemberHandle;

/**
 * <a href="https://github.com/CryptoMorin/XSeries/tree/master/src/main/java/com/cryptomorin/xseries/reflection">Original Source Code</a>
 *
 * @author CryptoMorin
 * @author Despical
 * <p>
 * Created at 23.05.2024
 */
public abstract class ClassHandle implements Handle<Class<?>> {

    public abstract ClassHandle asArray(int dimensions);

    public final ClassHandle asArray() {
        return asArray(1);
    }

    public abstract boolean isArray();

    public int getDimensionCount() {
        int count = -1;
        Class<?> clazz = unreflect();
        if (clazz == null) return count;

        do {
            clazz = clazz.getComponentType();
            count++;
        } while (clazz != null);

        return count;
    }

    public MethodMemberHandle method() {
        return new MethodMemberHandle(this);
    }

    public FieldMemberHandle field() {
        if (ReflectionUtils.VER < 9) {
            return new FieldMemberHandleJava8(this);
        }
        return new FieldMemberHandle(this);
    }

    public FieldMemberHandle getterField() {
        return field().getter();
    }

    public FieldMemberHandle setterField() {
        return field().setter();
    }

    public ConstructorMemberHandle constructor() {
        return new ConstructorMemberHandle(this);
    }

    public ConstructorMemberHandle constructor(Class<?>... parameters) {
        return constructor().parameters(parameters);
    }

    public ConstructorMemberHandle constructor(ClassHandle... parameters) {
        return constructor().parameters(parameters);
    }
}