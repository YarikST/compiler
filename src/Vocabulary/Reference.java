package Vocabulary;

/**
 * Created by admin-iorigins on 04.04.17.
 */
public class Reference extends BaseType {
    public static final int size = 4;
    private BaseType baseType;

    public Reference(BaseType baseType) {
        super(baseType.getLex(), baseType.getType(), size);
        this.baseType = baseType;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    @Override
    public String toString() {
        return baseType.toString();
    }
}
