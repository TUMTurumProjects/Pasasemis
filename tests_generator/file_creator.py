import json
import os
import logging
from shutil import rmtree


def create_spec_file(number_of_test):
    spec = {'number': number_of_test, 'type': 0}


def insert_info(number, testing_class_name, method_name, console_input_stream, object_to_test, args,
                attributes_to_check):
    output = (
        f'class Test{number} ' '{\n'
        f'    public String testingClassName = "{testing_class_name}";\n'
        f'    public String methodName = "{method_name}";\n'
        f'    public String consoleInputStream = "{console_input_stream}";\n'
        '    \n'
        '    public Object object;\n'
        '    public Object[] args;\n'
        '    public String[] attributesToCheck = new String[] {' f'{attributes_to_check}' '}' 
        '    \n'
        f'    public Test{number} () ' '{\n'
        f'        object = {object_to_test};\n'
        '        args = new Object[]{' f'{args}' '};\n'
        '    }\n'
        '}'
    )

    return output


def save_file(problem_id, number, text):
    """
    Save .java file. Overwrites old files!
    :param problem_id: ID of the problem
    :param number: number of test
    :param text: class text
    """
    with open(f'output/{problem_id}/Test{number}.java', 'w') as f:
        f.write(text)


def create_folders(folders):
    """
    Create necessary folders.
    :param folders: list of directory names
    """
    for folder in folders:
        if not os.path.isdir(folder):
            os.makedirs(folder)
            logging.info(f'Created directory: {folder}')


def clean_folders(folders):
    """
    Clean given directories.
    :param folders: list of directory names
    """
    for folder in folders:
        rmtree(folder)
    create_folders(folders)


def create_config(path, number_of_tests):
    """
    Create config file.
    :param path: where to save config
    :param number_of_tests: how many tests were generated
    """
    config = {'number': number_of_tests, 'type': 0}
    with open(f'{path}config.json', 'w') as f:
        json.dump(config, f)


if __name__ == '__main__':
    pass
