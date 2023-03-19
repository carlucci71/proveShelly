package shellyEM.lambda;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Formazione {

	enum Ruolo{Por, Dc, Dd, Ds, E, W, C, T, M, A, Pc};
	
	public static void main(String[] args) {
		Formazione f = new Formazione();
		f.go();
	}

	
	
	class Giocatore{
		private List<Ruolo> ruoli;
		private String nome;
		private String squadra;
		public Giocatore(String ruoli, String nome, String squadra) {
			this.ruoli=Arrays.asList(ruoli.split(";")).stream().map(el -> Ruolo.valueOf(el)).collect(Collectors.toList());
			this.nome=nome;
			this.squadra=squadra;
		}
		public List<Ruolo> getRuoli() {
			return ruoli;
		}
		public String getNome() {
			return nome;
		}
		public String getSquadra() {
			return squadra;
		}
		@Override
		public String toString() {
			return "Giocatore [ruoli=" + ruoli + ", nome=" + nome + ", squadra=" + squadra + "]";
		}
	}

	class Modulo {
		private String nome;
		private List<List<Ruolo>> ruoli;
		Modulo(String nome, List<String> ruoli){
			this.nome=nome;
			this.ruoli = new ArrayList<>(); 
			ruoli.stream().forEach (el -> {
				List<Ruolo> rr=new ArrayList<>();
				List<String> split = Arrays.asList(el.split(";"));
				split.forEach(r -> rr.add(Ruolo.valueOf(r)));
				this.getRuoli().add(rr);
			});
		}
		@Override
		public String toString() {
			return "Modulo [nome=" + getNome() + ", ruoli=" + getRuoli() + "]";
		}
		public List<List<Ruolo>> getRuoli() {
			return ruoli;
		}
		public String getNome() {
			return nome;
		}
	}
	
	private void go() {
		List<Giocatore> giocatori=new ArrayList<>();
//		giocatori.add(new Giocatore("Por","Handanovic","Int"));
//		giocatori.add(new Giocatore("Por","Cordaz","Int"));
//		giocatori.add(new Giocatore("Por","Onana","Int"));
		giocatori.add(new Giocatore("Dd;Ds;E","Hysaj","Laz"));
		giocatori.add(new Giocatore("Dc","Bonucci","Juv"));
		giocatori.add(new Giocatore("Dd;Dc","Milenkovic","Fio"));
		giocatori.add(new Giocatore("Dc","Bremer","Juv"));
		giocatori.add(new Giocatore("Ds;Dc","Igor","Fio"));
		giocatori.add(new Giocatore("Ds;E","Augello","Sam"));
		giocatori.add(new Giocatore("Dd;E","Mazzocchi","Sal"));
		giocatori.add(new Giocatore("Ds;E","Doig","Ver"));
		giocatori.add(new Giocatore("Dd;E","Dodo'","Fio"));
		giocatori.add(new Giocatore("E;W","Lazovic","Ver"));
		giocatori.add(new Giocatore("C;T","Milinkovic-Savic","Laz"));
		giocatori.add(new Giocatore("E;W","Cuadrado","Juv"));
		giocatori.add(new Giocatore("M;C","Lopez M.","Sas"));
		giocatori.add(new Giocatore("E;W","Kostic","Juv"));
		giocatori.add(new Giocatore("T;A","Miranchuk","Tor"));
		giocatori.add(new Giocatore("M;C","Ricci S.","Tor"));
		giocatori.add(new Giocatore("M;C","Coulibaly L.","Sal"));
		giocatori.add(new Giocatore("M;C","Hjulmand","Lec"));
		giocatori.add(new Giocatore("Pc","Sanabria","Tor"));
		giocatori.add(new Giocatore("A","Berardi","Sas"));
		giocatori.add(new Giocatore("A","Di Maria","Juv"));

		List<Modulo> moduli=new ArrayList<>();
		moduli.add(new Modulo("3412",Arrays.asList("Dc","Dc","Dc","E","M;C","C","E","T","A;Pc","A;Pc")));

		moduli.forEach(modulo -> {
			List<Giocatore> schierabili = new ArrayList<>();
			List<List<Ruolo>> ruoli = modulo.getRuoli();
			ruoli.forEach(lr -> {
				lr.forEach(r -> {
					System.out.println(r + " -> " + giocatori.stream().filter(g -> g.getRuoli().contains(r)).collect(Collectors.toList()));
				});
			});
		});
	}
}
