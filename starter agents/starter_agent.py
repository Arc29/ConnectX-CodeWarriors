import random
import numpy as np

inarow = int(input().strip())
grid_dimensions = [int(x) for x in input().strip().split(" ")]
n = grid_dimensions[0]
m = grid_dimensions[1]
grid = np.zeros((n,m))
for i in range(n):
    row = [int(x) for x in input().strip().split(" ")]
    for j in range(m):
        grid[i][j] = row[j]
last = [int(x) for x in input().strip().split(" ")]
last_player = last[0]
last_player_move = (last[1], last[2])

class Configuration:
    def __init__(self, rows, columns, inarow):
        self.rows = rows
        self.columns = columns
        self.inarow = inarow
class Observation:
    def __init__(self, grid, last_player, last_player_move):
        self.board = grid
        self.mark = 3 - last_player
        self.last_player_move_pos = last_player_move

def get_actual_coords(loc_board, pos):
    return (((loc_board//3)*3)+pos//3, loc_board%3*3+pos%3)
    
def get_simple_coords(row,column):
    return (((row//3)*3)+column//3, row%3*3+column%3)

def get_valid_moves(board, config, last_player_move_pos):
    valid_moves = []
    current_local_board = None
    if last_player_move_pos[0] == -1 and last_player_move_pos[1] == -1:
        current_local_board = 4
    else:
        current_local_board = get_simple_coords(last_player_move_pos[0], last_player_move_pos[1])[1]

    for i in range(config.rows):
        actual_coords = get_actual_coords(current_local_board,i)
        if board[actual_coords[0]][actual_coords[1]] == 0:
            valid_moves.append((actual_coords[0], actual_coords[1]))
    return valid_moves


def agent(obs, config):
    valid_moves = get_valid_moves(obs.board, config, obs.last_player_move_pos)
    choice = random.choice(valid_moves)
    print(choice[0], choice[1])


configuration = Configuration(n, m, inarow)
observation = Observation(grid, last_player, last_player_move)
agent(observation, configuration)