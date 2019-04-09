package fr.insee.format;

public class Publication {
    private String title;
    private String chapo;
    private String corp;
    private String tail;

    public Publication(String title, String chapo, String corp, String tail) {
        this.title = title;
        this.chapo = chapo;
        this.corp = corp;
        this.tail = tail;
    }
}
