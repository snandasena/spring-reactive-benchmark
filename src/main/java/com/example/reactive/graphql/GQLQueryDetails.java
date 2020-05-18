package com.example.reactive.graphql;

public class GQLQueryDetails {

    private static final String BOOK_SCHEMA =
            "type Query{\n" +
                    "    findBook(id:ID):Book\n" +
                    "    findBookById(id:ID):Book\n" +
                    "    findBooks(limit:Int):[Book]\n" +
                    "}\n" +
                    "\n" +
                    "type Book{\n" +
                    "    id:ID\n" +
                    "    name:String\n" +
                    "    pageCount:Int\n" +
                    "    author: Author\n" +
                    "}\n" +
                    "\n" +
                    "type Author{\n" +
                    "    id:ID\n" +
                    "    firstName:String\n" +
                    "    lastName:String\n" +
                    "}\n";

    private String typeName;
    private String fieldName;
    private String schema;

    private int id;

    public GQLQueryDetails() {
        this.schema = BOOK_SCHEMA;
    }


    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getSchema() {
        return schema;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
