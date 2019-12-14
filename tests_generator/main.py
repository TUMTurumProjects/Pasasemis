import file_creator
import logging

logging.basicConfig(level=logging.INFO)

if __name__ == '__main__':
    problem_id = 'w07h01'
    number_of_test = 0

    # Create necessary folders
    file_creator.create_folders(['output', f'output/{problem_id}'])
    file_creator.clean_folders([f'output/{problem_id}'])

    #  Section *********************************************************************************************************
    inputs = [[3, 3, 0, 1], [3, 3, 0, 2], [3, 3, 0, 2]]

    for counter in range(number_of_test, number_of_test + len(inputs)):
        test_input = inputs[counter]
        console_input = ''
        object_to_test = f'new RgbColor({test_input[0]}, {test_input[1]}, {test_input[2]}, {test_input[3]})'
        args = ''

        class_text = file_creator.insert_info(counter, 'RgbColor', 'toRgbColor8Bit', console_input, object_to_test,
                                              args)

        file_creator.save_file(problem_id, counter, class_text)

        logging.info(f'Created: Test{counter}')

    number_of_test += len(inputs)
    #  Section *********************************************************************************************************

    # Create config
    file_creator.create_config(f'output/{problem_id}/', number_of_test)
