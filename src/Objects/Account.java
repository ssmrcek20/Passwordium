package Objects;

public class Account {
    public int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNaziv() {
        return Naziv;
    }

    public void setNaziv(String naziv) {
        Naziv = naziv;
    }

    public String getKorIme() {
        return KorIme;
    }

    public void setKorIme(String korIme) {
        KorIme = korIme;
    }

    public String getLozinka() {
        return Lozinka;
    }

    public void setLozinka(String lozinka) {
        Lozinka = lozinka;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String Naziv;
    public String KorIme;
    public String Lozinka;
    public String Link;

    public Account(String naziv, String korIme, String lozinka, String link) {
        Naziv = naziv;
        KorIme = korIme;
        Lozinka = lozinka;
        Link = link;
    }

    public Account(String naziv, String korIme, String lozinka){
        Naziv = naziv;
        KorIme = korIme;
        Lozinka = lozinka;
    }
}
