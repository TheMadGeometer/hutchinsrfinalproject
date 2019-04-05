jQuery(document).on('ready', function() {
    
    getStandings("Atlantic");
    getStandings("Metropolitan");
    getStandings("Central");
    getStandings("Pacific");
    
  jQuery('form#add_game_form').bind('submit', function(event){
    event.preventDefault();
    
    var form = this;
    var json = JSON.stringify(ConvertAddGameFormToJSON(form));
    var tgame = $('#game_table tr:last');

    $.ajax({
        contentType: 'application/json',
        type: "POST",
        data: json,
        dataType: "json",
        url: "http://localhost:8080/games"
    }).complete(function(response) {
        //console.log("Done!");
        updateChiclets();
    });
      
      
    return true;
  });  
    
    jQuery('form#add_goal_form').bind('submit', function(event){
    event.preventDefault();
    
    var form = this;
    var json = JSON.stringify(ConvertAddGoalFormToJSON(form));

    $.ajax({
        contentType: 'application/json',
        type: "POST",
        data: json,
        dataType: "json",
        url: "http://localhost:8080/goals"
    }).complete(function(response) {
        //console.log("goal added");
        updateChiclets();
    });
      
      return true;
  });
    
    jQuery('form#end_game_form').bind('submit', function(event){
        event.preventDefault();
        var form = this;
        var json = convertEndGameFormToJSON(form);
        var gameId = json['gameId'];
        delete json.gameId;
        
        var jsonString = JSON.stringify(json)
        //console.log(`hitting endpoint: http://localhost:8080/games/${gameId}`);
        
        $.ajax({
            contentType: "application/json",
            type: "PUT",
            data: jsonString,
            dataType: "json",
            url:`http://localhost:8080/games/${gameId}`
        }).complete(function() {
            removeChiclets();
            //console.log("updating standings");
            getStandings("Atlantic");
            getStandings("Metropolitan");
            getStandings("Central");
            getStandings("Pacific");
            //console.log("standings updated");
        });
    });
    
    jQuery('form#get_active_games_form').bind('submit', function(event) {
        event.preventDefault();
        updateChiclets();
    });
    
    jQuery('form#remove_chiclets_form').bind('submit', function(event) {
        event.preventDefault();
        removeChiclets();
    });
    
});

function ConvertAddGameFormToJSON(form) {
    var array = jQuery(form).serializeArray();
    var json = {};
    
    jQuery.each(array, function() {
       json[this.name] = this.value; 
    });
    json['homeTeamScore'] = 0;
    json['awayTeamScore'] = 0;
    json['isDone'] = false;
    json['wasOvertimeGame'] = false;
    //console.log(json);
    return json;
}

function ConvertAddGoalFormToJSON(form) {
    var array = jQuery(form).serializeArray();
    var json = {};
    
    jQuery.each(array, function() {
        //console.log(`ADDING GOAL: ${this.name}, ${this.value}`);
       json[this.name] = this.value; 
    });
    
    //console.log(json);
    return json;
}

function convertEndGameFormToJSON(form) {
    var array = jQuery(form).serializeArray();
    var json = {}
    
    jQuery.each(array, function() {
        json[this.name] = this.value;
    });
    
    var wentToOvertime = (json['wasOvertimeGame'] == 'true');
    json['wasOvertimeGame'] = wentToOvertime;
    
    return json;
}

function getGameDataFromServer() {
    var result = $.ajax({
        contentType: 'application/json',
        type: "GET",
        dataType: "json",
        url: "http://localhost:8080/games"
    });
    return result;
}

function updateChiclets() {
    var jqXHR = $.getJSON("http://localhost:8080/games/active");
    jqXHR.complete(function(gameData) {
        var gameWrapper = gameData['responseJSON'];
        var games = gameWrapper['games'];
        var table = $('#game_table tbody');
        
        jQuery.each(games, function() {
            if($('#game_' + this['gameId']+"_home_row").length) {
                $('#game_' + this['gameId'] + "_home_score").html(this['homeTeamScore']);
                $('#game_' + this['gameId'] + "_away_score").html(this['awayTeamScore']);
            } else {
                table.append(`<tr id=game_${this['gameId']}_id_row><td>Game ID: </td> <td>${this['gameId']}</td> </tr>`);
                table.append(`<tr id=game_${this['gameId']}_home_row>
                                    <td id=game_${this['gameId']}_home_team>${this['homeTeamName']}</td>
                                    <td id=game_${this['gameId']}_home_score>${this['homeTeamScore']}</td>
                                </tr>`);
                table.append(`<tr id=game_${this['gameId']}_away_row>
                                    <td id=game_${this['gameId']}_away_team>${this['awayTeamName']}</td>
                                    <td id=game_${this['gameId']}_away_score>${this['awayTeamScore']}</td>
                                </tr>`);
            }
        });
        removeChiclets();
    });
}

function removeChiclets() {
    var jqXHR = $.getJSON("http://localhost:8080/games");
    jqXHR.complete(function(gameData) {
        var gameWrapper = gameData['responseJSON'];
        var games = gameWrapper['games'];
        jQuery.each(games, function() {
            if(this['isDone'] == true) {
                if($('#game_'+this['gameId']+'_id_row').length) {
                    $('#game_'+this['gameId']+'_id_row').remove();
                }
                if($('#game_'+this['gameId']+'_home_row').length) {
                    $('#game_'+this['gameId']+'_home_row').remove();
                }
                if($('#game_'+this['gameId']+'_away_row').length) {
                    $('#game_'+this['gameId']+'_away_row').remove();
                }
           }
        });
    });
}

function getStandings(division) {
    var jqXHR = $.getJSON(`http://localhost:8080/standings/${division}`);
    var lowercaseDivision = division.toLowerCase();
    var table = $(`#${lowercaseDivision}_standings_table tbody`);
    table.empty();
    jqXHR.complete(function(teamData){
        var standingsWrapper = teamData['responseJSON'];
        var standings = standingsWrapper['teamSeasons'];
        standings.sort((a, b) => (a.points > b.points) ? -1 : (a.points < b.points) ? 1 : (a.goalDifferential < b.goalDifferential) ? 1 : -1 );
        
        jQuery.each(standings, function(){
        table.append(`<tr id="${this['teamId']}_record">
            <td id="${this['teamId']}_year">${this['seasonYear']}</td>
            <td id="${this['teamId']}">${this['teamId']}</td>
            <td id="${this['teamId']}">${this['gamesPlayed']}</td>
            <td id="${this['teamId']}_wins">${this['wins']}</td>
            <td id="${this['teamId']}_losses">${this['losses']}</td>
            <td id="${this['teamId']}_overtime_losses">${this['overtimeLosses']}</td>
            <td id="${this['teamId']}_points">${this['points']}</td>
            <td id="${this['teamId']}_goals_for">${this['goalsFor']}</td>
            <td id="${this['teamId']}_goals_against">${this['goalsAgainst']}</td>
            <td id="${this['teamId']}_goal_differential">${this['goalDifferential']}</td>
        </tr>`);
        });
               
        
    });
}
