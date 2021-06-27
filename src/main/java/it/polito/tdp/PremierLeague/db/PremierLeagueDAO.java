package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	public List<Arco> getArchi() {
		String sql = "SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, (SUM(a1.TimePlayed) - SUM(a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 " + "WHERE a1.PlayerID<>a2.PlayerID " + "AND a1.`Starts`=1 "
				+ "AND a2.`Starts`=1 " +"AND a1.MatchID = a2.MatchID  "+"AND a1.TeamID<>a2.TeamID " +"GROUP BY a1.PlayerID, a2.PlayerID";
		List<Arco> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				Arco arco = new Arco(res.getInt("id1"),res.getInt("id2"),res.getInt("peso"));
				result.add(arco);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Player> getPlayersFromAvg(double x) {
		String sql = "SELECT a.PlayerID,p.Name, AVG(Goals) AS media " + "FROM actions a, players p "
				+ " WHERE p.PlayerID = a.PlayerID" + " GROUP BY a.PlayerID, p.Name " + "HAVING media>?";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();

			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));

				result.add(player);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Player> listAllPlayers() {
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));

				result.add(player);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Action> listAllActions() {
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"), res.getInt("MatchID"), res.getInt("TeamID"),
						res.getInt("Starts"), res.getInt("Goals"), res.getInt("TimePlayed"), res.getInt("RedCards"),
						res.getInt("YellowCards"), res.getInt("TotalSuccessfulPassesAll"),
						res.getInt("totalUnsuccessfulPassesAll"), res.getInt("Assists"),
						res.getInt("TotalFoulsConceded"), res.getInt("Offsides"));

				result.add(action);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
