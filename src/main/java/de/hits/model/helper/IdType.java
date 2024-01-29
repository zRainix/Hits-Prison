package de.hits.model.helper;

public enum IdType {

    UUID(String.class),
    NUMBER(Integer.class, Long.class);

    private Class<?>[] classes;

    private IdType(Class<?>... classes) {
        this.classes = classes;
    }

    public Class<?>[] getClasses() {
        return classes;
    }
}
