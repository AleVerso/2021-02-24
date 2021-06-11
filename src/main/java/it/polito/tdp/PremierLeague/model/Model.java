package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {

	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;

	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<>();
	}

	public void creaGrafo(Match m) {
		// creo grafo
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		// aggiungo i vertici
		dao.listAllPlayers(idMap);
		dao.getVertices(idMap, m.getMatchID());
		Graphs.addAllVertices(grafo, dao.getVertices(idMap, m.getMatchID()));

		// aggiungo archi
		for (Adiacenza a : dao.getAdiacenze(idMap, m.getMatchID())) {
			if (a.getPeso() >= 0) {
				// p1 meglio di p2, inserisco arco uscente da p1
				if (grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2(), a.getPeso());
				}
			} else {
				// p2 meglio di p1
				if (grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(grafo, a.getP2(), a.getP1(), (-1)*a.getPeso());
				}
			}

		}

		/*
		 * Stampo risultati System.out.println("Grafo creato");
		 * System.out.println("# Vertici: " + grafo.vertexSet().size());
		 * System.out.println("# Archi: " + grafo.edgeSet().size());
		 */

	}

	public Integer getNVertices() {
		return this.grafo.vertexSet().size();
	}

	public Integer getNEdge() {
		return this.grafo.edgeSet().size();
	}

	public List<Match> listAllMatches() {
		return dao.listAllMatches();
	}

	public GiocatoreMigliore getGiocatoreMigliore() {
		if (grafo == null) {
			return null;
		}

		Player best = null;
		Double maxDelta = (double)Integer.MIN_VALUE;

		for (Player p1 : this.grafo.vertexSet()) {
			// calcolo il delta del giocatore
			double pesoUscente = 0.0;
			for (DefaultWeightedEdge e : grafo.outgoingEdgesOf(p1)) {
				pesoUscente += grafo.getEdgeWeight(e);
			}
			double pesoEntrante = 0.0;
			for (DefaultWeightedEdge e : grafo.incomingEdgesOf(p1)) {
				pesoEntrante += grafo.getEdgeWeight(e);
			}
			double delta = pesoUscente - pesoEntrante;
			if (delta > maxDelta) {
				maxDelta = delta;
				best = p1;
			}
		}

		return new GiocatoreMigliore(best, maxDelta);
	}

	public Graph<Player, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

}
