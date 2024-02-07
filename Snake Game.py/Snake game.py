import random
import curses

def main(stdscr):
    # Initialize colors if supported
    if curses.has_colors():
        curses.start_color()
        curses.init_pair(1, curses.COLOR_GREEN, curses.COLOR_BLACK)
    
    # Set up the window borders
    stdscr.border(0)

    # Hide cursor
    curses.curs_set(False)

    # Main loop variables
    width = stdscr.getmaxyx()[1] - 2
    height = stdscr.getmaxyx()[0] - 2
    snk_x = width // 4
    snk_y = height // 2
    snake = [
        [snk_y, snk_x],
        [snk_y, snk_x - 1],
        [snk_y, snk_x - 2]
    ]
    food = [height//2, width//2]
    direction = (0, 1)
    change_direction = direction

    while True:
        next_key = stdscr.getch()
        key = key_mapper(next_key, change_direction)

        if key == 'Q':
            break
        
        snake.append(move_snake(key, snake))

        if is_collision(snake, food):
            create_food(food)
            spawn_apple(width, height)
        else:
            snake.pop(0)

        draw_objects(stdscr, snake, food)

def key_mapper(next_key, prev_dir):
    if next_key == curses.KEY_UP and prev_dir != (0, 1):
        return (0, -1)
    elif next_key == curses.KEY_DOWN and prev_dir != (0, -1):
        return (0, 1)
    elif next_key == curses.KEY_LEFT and prev_dir != (1, 0):
        return (-1, 0)
    elif next_key == curses.KEY_RIGHT and prev_dir != (-1, 0):
        return (1, 0)
    else:
        return prev_dir

def move_snake(key, snake):
    head = snake[0]
    x, y = head[0] + key[0], head[1] + key[1]

    if x < 0 or y < 0 or x > len(snake) or y > len(snake[0]):
        return None

    new_head = [x, y]
    snake.insert(0, new_head)
    return new_head

def is_collision(snake, food):
    snake_pos = set([tuple(item) for item in snake])
    food_pos = tuple(food)
    return food_pos in snake_pos

def create_food(food):
    sh, sw = curses.getsyx()
    food[:] = [random.randint(1, sh-1), random.randint(1, sw-1)]

def draw_objects(stdscr, snake, food):
    for segment in snake:
        try:
            stdscr.addch(*segment, curses.ACS_CKBOARD)
        except curses.error:
            pass

    stdscr.addch(*food, curses.ACS_PI)

def spawn_apple(width, height):
    apple_count = sum(len(row) for row in open('apples'))
    if apple_count % 3 == 0:
        apples.write("\n{} {}".format(random.randint(1, height-1), random.randint(1, width-1)))

curses.wrapper(main)