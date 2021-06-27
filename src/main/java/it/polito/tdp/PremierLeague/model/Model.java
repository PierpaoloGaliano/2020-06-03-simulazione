package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.sun.scenario.effect.DelegateEffect;

import it.polito.tdp.PremierLeague.model.*;
import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	Graph<Player, DefaultWeightedEdge> grafo;
	Map<Integer, Player> idMapGiocatori;
	PremierLeagueDAO dao = new PremierLeagueDAO();
	int bestDegree;
	List<Player> dreamTeam;

	public String creaGrafo(double x) {
		idMapGiocatori = new HashMap<>();
		List<Player> playerPerMedia = new ArrayList<>(dao.getPlayersFromAvg(x));
		this.grafo = new SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for (Player p : playerPerMedia) {
			idMapGiocatori.put(p.getPlayerID(), p);
		}
		Graphs.addAllVertices(this.grafo, playerPerMedia);

		for (Arco a : dao.getArchi()) {
			if (idMapGiocatori.containsKey(a.getPlayerID1()) && idMapGiocatori.containsKey(a.getPlayerID2())
					&& a.getPeso() != 0) {
				if (a.getPeso() < 0) {
					Graphs.addEdgeWithVertices(this.grafo, idMapGiocatori.get(a.getPlayerID2()),
							idMapGiocatori.get(a.getPlayerID1()), -a.getPeso());
				}
				if (a.getPeso() > 0) {
					Graphs.addEdgeWithVertices(this.grafo, idMapGiocatori.get(a.getPlayerID1()),
							idMapGiocatori.get(a.getPlayerID2()), a.getPeso());
				}
			}
		}

		return "GRAFO CREATO CON: " + this.grafo.vertexSet().size() + " VERTICI e " + this.grafo.edgeSet().size()
				+ " ARCHI\n";
	}

	public Player topPlayer() {
		int max = 0;
		Player topPlayer = null;
		for (Player p : this.grafo.vertexSet()) {
			if (this.grafo.outDegreeOf(p) > max) {
				max = this.grafo.outDegreeOf(p);
				topPlayer = p;
			}
		}
		return topPlayer;
	}

	public List<Opponent> avversariBattutiDa(Player topPlayer) {
		List<Opponent> result = new ArrayList<>();

		for (DefaultWeightedEdge edge : grafo.outgoingEdgesOf((it.polito.tdp.PremierLeague.model.Player) topPlayer)) {
			result.add(new Opponent(grafo.getEdgeTarget(edge), (int) grafo.getEdgeWeight(edge)));
		}
		Collections.sort(result);

		return result;
	}

	public List<Player> getDreamTeam(int k) {
		this.bestDegree = 0;
		this.dreamTeam = new ArrayList<>();
		List<Player> parziale = new ArrayList<Player>();

		this.ricorsione(parziale, new ArrayList<>(this.grafo.vertexSet()), k);

		return dreamTeam;
	}

	public void ricorsione(List<Player> parziale, List<Player> giocatori, int k) {
		if (parziale.size() == k) {
			int grado = this.getDegree(parziale);
			if(grado > this.bestDegree) {
				dreamTeam = new ArrayList<>(parziale);
				bestDegree = grado;
			}
			return;
		}
		for(Player p : giocatori) {
			if(!parziale.contains(p)) {
				parziale.add(p);
				//i "battuti" di p non possono più essere considerati
				List<Player> remainingPlayers = new ArrayList<>(giocatori);
				remainingPlayers.removeAll(Graphs.successorListOf(grafo, p));
				ricorsione(parziale, remainingPlayers, k);
				parziale.remove(p);
			}
		}
	}

	private int getDegree(List<Player> team) {
		int degree = 0;
		int in;
		int out;
		for (Player p : team) {
			in = 0;
			out = 0;
			for (DefaultWeightedEdge edge : this.grafo.outgoingEdgesOf(p)) {
				out += this.grafo.getEdgeWeight(edge);
			}
			for (DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(p)) {
				in += this.grafo.getEdgeWeight(edge);
			}
			degree+=(out-in);
		}
		return degree;
	}
}
