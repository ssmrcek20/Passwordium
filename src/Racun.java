public class Racun {
    public String Naziv;
    public String KorIme;
    public String Lozinka;
    public String Link;

    public Racun(String naziv, String korIme, String lozinka, String link) {
        Naziv = naziv;
        KorIme = korIme;
        Lozinka = lozinka;
        Link = link;
    }

    public Racun(String naziv, String korIme, String lozinka){
        Naziv = naziv;
        KorIme = korIme;
        Lozinka = lozinka;
    }
}
