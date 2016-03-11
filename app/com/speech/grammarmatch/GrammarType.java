package com.speech.grammarmatch;

/**
 * Enum that maps grammar names to their grammar file
 */
public enum GrammarType {
    CURRENCY,
    DATE,
    NUMBER,
    ORDINAL,
    TIME;

    public String grammarName() { return this.name().toLowerCase(); }

//    public GrammarType getType( String name ) {
//        GrammarType type;
//        switch (name.toLowerCase()) {
//            case "currency":
//                type = GrammarType.CURRENCY;
//                break;
//            case "date":
//                type = GrammarType.DATE;
//                break;
//            case "number":
//                type = GrammarType.NUMBER;
//                break;
//            case "ordinal":
//                type = GrammarType.ORDINAL;
//                break;
//            case "time":
//                type = GrammarType.TIME;
//                break;
//            default:
//                throw new IllegalArgumentException( "unknown grammar name " + name );
//
//        }
//        return type;
//    }
}
