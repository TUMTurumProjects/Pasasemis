# Other modules
import zipfile
import logging
from shutil import copyfile, rmtree, copytree, copy
from glob import glob
import os
import datetime
import random
import json


def get_result(path):
    """
    Read result from JSON file
    :param path: path to temporary folder
    :return: JSON dict with the result
    """
    json_file = f'{path}result.json'
    with open(json_file) as f:
        data = json.load(f)

    return data


def copy_test_cases(path_from, problem_id, path_to):
    """
    Find test cases file for the problem and move it to the given folder.
    :param path_from: where to look for tests
    :param problem_id: ID of the problem
    :param path_to: where to put tests
    :return: true if found, false otherwise
    """
    if len(problem_id) == 6 and problem_id[0] == 'w' and len(glob(f'{path_from}{problem_id}.*')) == 2:
        for file in glob(f'{path_from}{problem_id}.*'):
            if file[-5:] == ".json":
                copyfile(file, f'{path_to}config.json')
            else:
                copy(file, f'{path_to}src/main/java/')
        return True
    return False


def copy_master_solution(path_from, problem_id, path_to):
    """
    Find master solution directory for the problem and move it to the given folder.
    :param path_from: where to look for master solutions
    :param problem_id: ID of the problem
    :param path_to: where to put the solution folder
    :return: true if found, false otherwise
    """
    if len(glob(f'{path_from}{problem_id}/')) == 1:
        copytree(f'{path_from}{problem_id}/', path_to)


def create_folders(folders):
    """
    Create necessary folders.
    :param folders: list of directories to be created
    """
    for folder in folders:
        if not os.path.isdir(folder):
            os.makedirs(folder)
            logging.info(f'Created directory: {folder}')


def create_test_case_folder(path_to_tmp):
    """
    Create folder for temporary files of a test. Name is time dependent and includes a random number for extra security.
    :param path_to_tmp: path to the tmp folder
    :return: name of the folder, path to folder
    """
    name = datetime.datetime.now().strftime("%Y%m%d%H%M%S%f")
    name += str(random.randint(0, 10000)) + '/'
    folder_path = path_to_tmp + name
    os.makedirs(f'{path_to_tmp}{name}')
    logging.info(f'Created directory: {folder_path}')
    return name, folder_path


def download_file(document, path):
    """
    Download the file and return its path.
    :param document: Telegram Document
    :param path: where to save the file
    :return: path to saved file, file name, problem ID
    """
    file = document.get_file()

    file.download(custom_path=f'{path}{document.file_name}', timeout=15)

    problem_id = document.file_name[document.file_name.find('-') - 6:document.file_name.find('-')]
    logging.info(f'File downloaded: {document.file_name}, Problem ID: {problem_id}')

    return f'{path}{document.file_name}', document.file_name, problem_id


def unzip_file(zip_file_path, unpacked_file_path):
    """
    Unzip file.
    :param zip_file_path: path to file
    :param unpacked_file_path: where to save the unpacked file
    """
    with zipfile.ZipFile(zip_file_path, 'r') as zip_ref:
        zip_ref.extractall(unpacked_file_path)

    os.remove(zip_file_path)
    logging.info('File unzipped successfully')


def filter_directories(path, directories):
    """
    Find needed directories, copy them to root, delete everything else.
    :param path: path to folder to search in
    :param directories: list of directory names to copy to path
    """
    # Look for given directories, copy them to path
    for directory in directories:
        if len(glob(f'{path}**/{directory}/', recursive=True)) == 1:
            copytree(glob(f'{path}**/{directory}/', recursive=True)[0], f'{path}{directory}')
        else:
            return False

    # Delete everything other than given directories from path
    for deletee in glob(f'{path}*'):
        if deletee not in [f'{path}{directory}' for directory in directories]:
            rmtree(deletee)
    return True


def move_testee_directories(path_from, directories, path_to):
    """
    Move directories needed for tests to some path.
    :param path_from: where to look for files
    :param directories: which directories to copy
    :param path_to: where to copy them
    """
    for directory in directories:
        copytree(f'{path_from}{directory}/', f'{path_to}{directory}/')
        rmtree(f'{path_from}{directory}/')


def change_package(path, to_add):
    """
    Add to_add to package path of Java files.
    :param path: where
    :param to_add: what to add
    """
    for file in glob(f'{path}**/*.java', recursive=True):
        with open(file, 'r') as f:
            contents = f.readlines()

        if contents[0].split()[0] == 'package':
            contents[0] = contents[0].split()[0] + ' ' + to_add + ' '.join(contents[0].split()[1:])
            contents = ''.join(contents)

            with open(file, 'w') as f:
                f.write(contents)


def make_classes_public(path):
    """
    Make all not private classes public.
    :param path: where
    """
    for file in glob(f'{path}**/*.java', recursive=True):
        with open(file, 'r') as f:
            contents = f.readlines()

        something_changed = False
        for index, line in enumerate(contents):
            if len(line.split()) > 1 and line.split()[0] == 'class':
                contents[index] = 'public class ' + ' '.join(line.split()[1:])
                something_changed = True

        if something_changed:
            with open(file, 'w') as f:
                f.write(''.join(contents))


if __name__ == '__main__':
    change_package('', 'master.')
