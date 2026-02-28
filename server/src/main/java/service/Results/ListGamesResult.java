package service.Results;

import model.GameData;

import java.util.Collection;

public record ListGamesResult(
        Collection<GameData> games
) {
}
