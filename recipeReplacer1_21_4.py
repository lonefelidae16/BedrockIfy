#!/usr/bin/python3

""" Recipe Replacer for Minecraft 1.21.4
Created at Dec 12, 2024 by lonefelidae16 <kow161206@gmail.com>

Usage:
> recipeReplacer1_21_4.py [--help | -h] [--test | -t] /path/to/target/recipe.json

"""

import json
import sys
import argparse

parser = argparse.ArgumentParser(
         prog='RecipeReplacer',
         description='Make recipe.json compatible with MC1.21.4')
parser.add_argument('filename')
parser.add_argument('-t', '--test', action='store_true', help='show result JSON to STDOUT, no changes to the specified file')
args = parser.parse_args()

if __name__ == '__main__':

    struct = {}
    replaced = False
    with open(args.filename, 'r') as target_file:
        struct = json.load(target_file)
        if not 'type' in struct or not 'result' in struct:
            exit()
        if struct['type'] == 'minecraft:crafting_shapeless':
            ingredient_arr = []
            for ing in struct['ingredients']:
                if 'item' in ing:
                    ingredient_arr.append(ing['item'])
                elif 'tag' in ing:
                    ingredient_arr.append('#' + ing['tag'])
            struct['ingredients'] = ingredient_arr
            replaced = True
        elif struct['type'] == 'minecraft:crafting_shaped':
            if not 'key' in struct:
                exit()
            for k in struct['key']:
                if 'item' in struct['key'][k]:
                    _item = struct['key'][k]['item']
                    del struct['key'][k]['item']
                    struct['key'][k] = _item
                    replaced = True
                elif 'tag' in struct['key'][k]:
                    _tag = '#' + struct['key'][k]['tag']
                    del struct['key'][k]['tag']
                    struct['key'][k] = _tag
                    replaced = True
        if type(struct['result']) is not str and not 'count' in struct['result']:
            struct['result']['count'] = 1
            replaced = True

    if replaced:
        if args.test:
            print(json.dumps(struct, indent=2))
        else:
            with open(args.filename, 'w') as target_file:
                json.dump(struct, target_file, indent=2)
    else:
        print(args.filename + ": no changes made")
