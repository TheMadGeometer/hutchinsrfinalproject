CREATE DATABASE IF NOT EXISTS nhl;
USE nhl;

DROP TABLE IF EXISTS player_stat_lines;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS goal_summaries;
DROP TABLE IF EXISTS team_seasons;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS teams;

CREATE TABLE IF NOT EXISTS teams
(
	team_id INT PRIMARY KEY AUTO_INCREMENT,
	team_abbreviation VARCHAR(3),
    team_name VARCHAR(255),
    home_city VARCHAR(255),
    home_arena VARCHAR(255),
    year_founded VARCHAR(4),
    conference VARCHAR(255),
    division VARCHAR(255),
    INDEX team_name_ix (team_name),
    INDEX team_abbreviation_ix (team_abbreviation)
);

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("BOS", "Boston Bruins", "Boston, Massachusetts", "TD Garden", "1924", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("BUF", "Buffalo Sabres", "Buffalo, New York", "KeyBank Center", "1970", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("DET", "Detroit Red Wings", "Detroit, Michigan", "Little Caesars Arena", "1926", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("FLA", "Florida Panthers", "Sunrise, Florida", "BB&T Center", "1993", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("MTL", "Montreal Canadiens", "Montreal, QC, Canada", "Bell Centre", "1909", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("OTT", "Ottawa Senators", "Ottawa, ON, Canada", "Canadian Tire Centre", "1992", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("TBL", "Tampa Bay Lightning", "Tampa, Florida", "Amalie Arena", "1992", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("TOR", "Toronto Maple Leafs", "Toronto, ON, Canada", "Scotiabank Arena", "1917", "Eastern", "Atlantic");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("CAR", "Carolina Hurricanes", "Raleigh, North Carolina", "PNC Arena", "1979", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("CBJ", "Columbus Blue Jackets", "Columbus, Ohio", "Nationwide Arena", "2000", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("NJD", "New Jersey Devils", "Newark, New Jersey", "Prudential Center", "1974", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("NYI", "New York Islanders", "Uniondale, New York", "Nassau Coliseum", "1972", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("NYR", "New York Rangers", "New York City, New York", "Madison Square Garden", "1926", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("PHI", "Philadelphia Flyers", "Philadelphia, Pennsylvania", "Wells Fargo Center", "1967", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("PIT", "Pittsburgh Penguins", "Pittsburgh, Pennsylvania", "PPG Paints Arena", "1967", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("WSH", "Washington Capitals", "Washington, D.C.", "Capital One Arena", "1974", "Eastern", "Metropolitan");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("CHI", "Chicago Blackhawks", "Chicago, Illinois", "United Center", "1926", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("COL", "Colorado Avalanche", "Denver, Colorado", "Pepsi Center", "1979", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("DAL", "Dallas Stars", "Dallas, Texas", "American Airlines Center", "1967", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("MIN", "Minnesota Wild", "St. Paul, Minnesota", "Scel Energy Center", "2000", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("NSH", "Nashville Predators", "Nashville, Tennessee", "Bridgestone Arena", "1998", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("STL", "St. Louis Blues", "St. Louis, Missouri", "Enterprise Center", "1967", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("WPG", "Winnipeg Jets", "Winnipeg, MN, Canada", "Bell MTS Place", "1999", "Western", "Central");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("ANA", "Anaheim Ducks", "Anaheim, California", "Honda Center", "1993", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("ARI", "Arizona Coyotes", "Glendale, Arizona", "Gila River Arena", "1979", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("CGY", "Calgary Flames", "Calgary, AB, Canada", "Scotiabank Saddledome", "1972", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("EDM", "Edmonton Oilers", "Edmonton, AB, Canada", "Rogers Place", "1979", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("LAK", "Los Angeles Kings", "Los Angeles, California", "Staples Center", "1967", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("SJS", "San Jose Sharks", "San Jose, California", "SAP Center", "1991", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("VAN", "Vancouver Canucks", "Vancouver, BC, Canada", "Rogers Arena", "1970", "Western", "Pacific");

INSERT INTO teams (team_abbreviation, team_name, home_city, home_arena, year_founded, conference, division) VALUES
("VGK", "Vegas Golden Knights", "Las Vegas, Nevada", "T-Mobile Arena", "2017", "Western", "Pacific");

CREATE TABLE IF NOT EXISTS games
(
	game_id INT PRIMARY KEY AUTO_INCREMENT,
    game_year VARCHAR(9),
    game_date VARCHAR(10),
    game_time VARCHAR(5),
    home_team_name VARCHAR(255) NOT NULL,
    home_team_score INT NOT NULL,
    away_team_name VARCHAR(255) NOT NULL, 
    away_team_score INT NOT NULL,
    is_done BOOLEAN DEFAULT FALSE,
    was_overtime_game BOOLEAN DEFAULT FALSE,
    CONSTRAINT home_team_fk FOREIGN KEY (home_team_name) REFERENCES teams (team_name) ON DELETE CASCADE,
    CONSTRAINT away_team_fk FOREIGN KEY (away_team_name) REFERENCES teams (team_name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS goal_summaries 
(
    goal_id INT PRIMARY KEY AUTO_INCREMENT,
    game_id INT,
    scoring_team_name VARCHAR(255),
    period_of_goal INT,
    time_of_goal VARCHAR(5),
    goal_scorer INT,
    assist_one INT,
    assist_two INT,
    CONSTRAINT goal_scored_in_game_fk FOREIGN KEY (game_id) REFERENCES games (game_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS team_seasons
(
	season_year VARCHAR(9),
    team_name VARCHAR(255),
    games_played INT,
    wins INT,
    losses INT,
    overtime_losses INT,
    points INT,
    goals_for INT,
    goals_against INT,
    goal_differential INT,
    PRIMARY KEY (season_year, team_name),
    CONSTRAINT teams_fk FOREIGN KEY (team_name) REFERENCES teams (team_name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS players
(
	player_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255),
	last_name VARCHAR(255),
	jersey_number VARCHAR(255),
	date_of_birth VARCHAR(255),
	birth_city VARCHAR(255),
	birth_state_province VARCHAR(255),
	birth_country VARCHAR(255),
	nationality VARCHAR(255),
	height VARCHAR(20),
	weight Int,
	current_team VARCHAR(255),
	primary_position VARCHAR(255),
	shoots_catches VARCHAR(1),
    CONSTRAINT plays_for_fk FOREIGN KEY (current_team) REFERENCES teams (team_name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS player_stat_lines
(
	season VARCHAR(9),
    team_name VARCHAR(255),
	player_id INT,
    games_played INT,
    goals INT,
    assists INT,
    points INT,
    plus_minus INT,
    penalty_minutes INT,
    CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES players (player_id) ON DELETE CASCADE,
    PRIMARY KEY (season, player_id)
);

DROP TRIGGER IF EXISTS game_update_trigger;
DELIMITER //
CREATE TRIGGER game_update_trigger
AFTER INSERT ON goal_summaries
FOR EACH ROW 
BEGIN
	UPDATE player_stat_lines SET goals=goals+1, points=points+1 WHERE player_id = NEW.goal_scorer;
    UPDATE player_stat_lines SET assists=assists+1, points=points+1 WHERE player_id = NEW.assist_one;
    UPDATE player_stat_lines SET assists=assists+1, points=points+1 WHERE player_id = NEW.assist_two;
	UPDATE games SET home_team_score=home_team_score+1 WHERE game_id=NEW.game_id AND home_team_name=NEW.scoring_team_name;
	UPDATE games SET away_team_score=away_team_score+1 WHERE game_id=NEW.game_id AND away_team_name=NEW.scoring_team_name;
END//
DELIMITER ;

DROP TRIGGER IF EXISTS after_game_result_added;
DELIMITER //
CREATE TRIGGER after_game_result_added
	AFTER UPDATE ON games
	FOR EACH ROW
	BEGIN
		IF NEW.is_done THEN
			IF NEW.home_team_score > NEW.away_team_score THEN
				UPDATE team_seasons SET wins = wins + 1, points = points + 2 WHERE team_name = NEW.home_team_name;
				IF NEW.was_overtime_game THEN
					UPDATE team_seasons SET overtime_losses = overtime_losses + 1, points = points + 1 WHERE team_name = New.away_team_name;
				ELSE
					UPDATE team_seasons SET losses=losses + 1 WHERE team_name = New.away_team_name;
				END IF;
			ELSE 
				UPDATE team_seasons SET wins = wins + 1, points=points + 2 WHERE team_name = New.away_team_name;
				IF NEW.was_overtime_game THEN
					UPDATE team_seasons SET overtime_losses = overtime_losses + 1, points = points + 1 WHERE team_name = New.home_team_name;
				ELSE
					UPDATE team_seasons SET losses = losses+1 WHERE team_name = New.home_team_name;
				END IF;
			END IF;
			UPDATE team_seasons SET goals_for = goals_for + NEW.home_team_score, goals_against = goals_against + New.away_team_score, 
				goal_differential = goal_differential + (NEW.home_Team_score - NEW.away_team_score) WHERE team_name = NEW.home_team_name;
			UPDATE team_seasons SET goals_for = goals_for + NEW.away_team_score, goals_against = goals_against + New.home_team_score, 
				goal_differential = goal_differential + (NEW.away_Team_score - NEW.home_team_score) WHERE team_name = NEW.away_team_name;
			UPDATE team_seasons SET games_played = games_played+1 WHERE NEW.home_team_name=team_name OR NEW.away_team_name=team_name;
			UPDATE player_stat_lines SET games_played=games_played+1 WHERE NEW.home_team_name=team_name OR NEW.away_team_name=team_name;
		END IF;
    END//
DELIMITER ;

DROP PROCEDURE IF EXISTS make_team_seasons_for_year;
DELIMITER //
CREATE PROCEDURE make_team_seasons_for_year
(
	season VARCHAR(9)
)
BEGIN
	DECLARE done INT DEFAULT FALSE;
    DECLARE id VARCHAR(255);
	DECLARE team_cursor CURSOR FOR SELECT team_name FROM teams;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN team_cursor;
    
    read_loop: LOOP
		FETCH team_cursor INTO id;
        IF done THEN 
			LEAVE read_loop;
		END IF;
        INSERT INTO team_seasons (season_year, team_name, games_played, wins, losses, overtime_losses, points, goals_for, goals_against, goal_differential)
			VALUES (season, id, 0, 0, 0, 0, 0, 0, 0, 0);
    END LOOP;
END//
DELIMITER ;

CALL make_team_seasons_for_year("2018-2019");

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'herewegoagain';