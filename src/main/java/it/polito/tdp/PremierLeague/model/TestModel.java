package it.polito.tdp.PremierLeague.model;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		
		PremierLeagueDAO dao = new PremierLeagueDAO();
		
		m.creaGrafo(dao.listAllMatches().get(0));
		System.out.println(m.getGiocatoreMigliore().toString());

	}

}
