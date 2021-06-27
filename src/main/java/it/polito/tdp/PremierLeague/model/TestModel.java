package it.polito.tdp.PremierLeague.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model m =new Model();
		System.out.println(m.creaGrafo(0.3));
		System.out.println(m.topPlayer());
		System.out.println(m.avversariBattutiDa(m.topPlayer()));
		System.out.println(m.getDreamTeam(2));
	}

}
