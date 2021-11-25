public class SchemaToken {
    String name;
    String tokenType;
    boolean isRequired;

    public SchemaToken(String name, String type, boolean isRequired) {
        this.name = name;
        this.tokenType = type;
        this.isRequired = isRequired;
    }

    @Override
    public String toString() {
        return name + " " + tokenType + " " + isRequired + "\n";
    }
}
