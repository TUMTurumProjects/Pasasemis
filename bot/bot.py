# Our modules
import credentials
import file_handler
import message_creator
import config

# Other modules
import logging
from subprocess import call, PIPE, Popen
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters, run_async
from shutil import rmtree, copytree
import time

logging.basicConfig(level=logging.INFO)


@run_async
def info_callback(update, context):
    update.message.reply_text('Want to test a PGDP solution before Sunday night? \n\nSend me a ZIP file from BitBucket '
                              'with any PGDP solution. To do this, first commit your Java program to Artemis, then '
                              'go to BitBucket '
                              '-> Project, find the problem you want to test, open it, go to your solution, '
                              'click ... -> Download and then drop the ZIP file here. \n\nOn Macs in Safari you may '
                              'want to right-click Download and select Download Linked File.')
    updater.bot.send_photo(chat_id=update.message.chat_id, photo=open(f'{config.STATIC_FOLDER}bitbucket.png', 'rb'))


@run_async
def text_callback(update, context):
    if update.message.chat_id in credentials.TRUSTED_ACCOUNTS:
        output, output_err = Popen(update.message.text, stdout=PIPE, stderr=PIPE, shell=True).communicate(timeout=60)
        if len(output.decode('UTF-8') + output_err.decode('UTF-8')) == 0:
            update.message.reply_text('Executed')
        else:
            update.message.reply_text(output.decode('UTF-8') + output_err.decode('UTF-8'))
    else:
        update.message.reply_text('I do not respond to text commands. Send me a ZIP file from BitBucket with any PGDP '
                                  'solution you want to test. Want to know '
                                  'how to find the ZIP file? Type /info')


@run_async
def start_callback(update, context):
    message_creator.send_gif(updater, update.message.chat_id, 'https://media.giphy.com/media/ASd0Ukj0y3qMM/giphy.gif')
    update.message.reply_text('Hi! I will help you with testing your PGDP programs.\n\n'
                              'Send me a ZIP file from BitBucket '
                              'with any PGDP solution you want to test. To do this, first commit your Java program '
                              'to Artemis, then go to BitBucket '
                              '-> Project, find the problem, open it, '
                              'click ... -> Download and then drop the ZIP file here. \n\nOn Macs in Safari you may '
                              'want to right-click Download and select Download Linked File.')

    updater.bot.send_photo(chat_id=update.message.chat_id, photo=open(f'{config.STATIC_FOLDER}bitbucket.png', 'rb'))


@run_async
def file_callback(update, context):
    """
    Parse received file, test it with Java, send response back.
    """
    if update.message.document.mime_type == 'multipart/x-zip' or update.message.document.mime_type == 'application/zip':
        # Create folder for temporary files
        tmp_folder_name, tmp_folder_path = file_handler.create_test_case_folder(config.TMP_FOLDER)

        # Download the file
        path_to_file, filename, problem_id = file_handler.download_file(update.message.document, tmp_folder_path)

        update.message.reply_text(f'Starting to parse: {filename}')

        # Unzip the file to tmp/testee
        file_handler.unzip_file(path_to_file, f'{tmp_folder_path}testee/')

        # Find needed directories, copy them to path, delete everything else.
        structure_ok = file_handler.filter_directories(tmp_folder_path, ['pgdp'])
        file_handler.change_package(tmp_folder_path, 'testee.')

        if structure_ok:
            logging.info('File structure ok')

            # Copy tests and testing files to the temporary folder
            copytree(config.TESTING_FILES_DIRECTORY, f'{tmp_folder_path}checker/')
            file_handler.move_testee_directories(tmp_folder_path, ['pgdp'],
                                                 f'{tmp_folder_path}checker/src/main/java/testee/')
            if file_handler.copy_test_cases(config.TESTS_FOLDER, problem_id, f'{tmp_folder_path}checker/'):
                # Copy master solution to the temporary folder
                file_handler.copy_master_solution(config.MASTER_SOLUTIONS_FOLDER, problem_id,
                                                  f'{tmp_folder_path}checker/src/main/java/master/')
                # Change package names and make not public classes public
                file_handler.change_package(f'{tmp_folder_path}checker/src/main/java/master/', 'master.')
                file_handler.make_classes_public(f'{tmp_folder_path}checker/src/main/java/master/')
                file_handler.make_classes_public(f'{tmp_folder_path}checker/src/main/java/testee/')

                try:
                    update.message.reply_text(f'Testing: {filename}')

                    call(['../judge/scripts/run.sh', f'{tmp_folder_path}checker/', problem_id])
                    result = file_handler.get_result(f'{tmp_folder_path}checker/')
                    message_creator.send_result_message(update.message, updater, result, problem_id)
                except:
                    update.message.reply_text('Something failed :( Drop us an email at '
                                              'tumturum.solution_checker@protonmail.com')
                    for account in credentials.TRUSTED_ACCOUNTS:
                        updater.bot.send_message(chat_id=account, text=f"Crash: {update.message.chat_id}")
            else:
                update.message.reply_text('Tests for this task have not been created yet. You can contribute to the '
                                          'development of the project by sending us your test cases at '
                                          'tumturum.solution_checker@protonmail.com, so that we can '
                                          'add them to the judging system.')
                logging.info(f'No tests found for the problem {problem_id}')
        else:
            update.message.reply_text(
                'Wrong file structure. Make sure you copy the ZIP file from BitBucket. Want to know '
                'how to find the ZIP file? Type /info')
            logging.info('Wrong file structure')

        # Delete temporary files
        rmtree(tmp_folder_path)

    else:
        update.message.reply_text('Unsupported file type. Please send me a ZIP file from BitBucket. First commit your '
                                  'solution to Artemis, then go to BitBucket '
                                  '-> Project, find the problem you want to test, open it, go to your solution, '
                                  'click ... -> Download and then drop the ZIP file here. On Macs in Safari you may '
                                  'want to right-click Download and select Download Linked File.')


# Create folders needed for execution
file_handler.create_folders([config.TMP_FOLDER])

# Set the bot up
updater = Updater(credentials.TELEGRAM_TOKEN, use_context=True)

updater.dispatcher.add_handler(CommandHandler('start', start_callback))
updater.dispatcher.add_handler(CommandHandler('info', info_callback))
updater.dispatcher.add_handler(MessageHandler(Filters.document, file_callback))
updater.dispatcher.add_handler(MessageHandler(Filters.text, text_callback))

updater.start_polling()
updater.idle()
