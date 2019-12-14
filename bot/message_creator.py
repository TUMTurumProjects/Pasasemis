# Our modules
import credentials

# Other modules
from requests import get


def send_result_message(message, updater, result, problem_id):
    """
    Create result message and send it.
    :param message: bot message
    :param updater: bot
    :param result: JSON dict with result
    :param problem_id: ID of the problem
    """
    if result['passed_overall']:
        send_random_gif(updater, message.chat_id, 'awesome')
        result_message = f"{problem_id}: passed {result['passed_tests']} in {result['time']} ms!"

        message.reply_text(result_message)
        for account in credentials.TRUSTED_ACCOUNTS:
            updater.bot.send_message(chat_id=account, text=f"This guy had a successful submit of the problem "
                                                           f"{problem_id}: {message.chat_id}")
    else:
        send_random_gif(updater, message.chat_id, 'lol')

        if len(result['error']) > 0:
            result_message = f"{problem_id}: System message: {result['error']}"

            message.reply_text(result_message)
            for account in credentials.TRUSTED_ACCOUNTS:
                updater.bot.send_message(chat_id=account, text=f"{result_message} {message.chat_id}")
        else:
            result_message = f"{problem_id}: passed {result['passed_tests']} in {result['time']} ms.\n" \
                             f"System message(s):\n\n"

            for line in result['messages']:
                result_message += "- " + line + "\n\n"
            message.reply_text(result_message)

            for account in credentials.TRUSTED_ACCOUNTS:
                updater.bot.send_message(chat_id=account, text=f"This guy pasasal on the problem {problem_id}: "
                                                               f"{message.chat_id}")


def send_random_gif(updater, chat_id, keyword):
    """
    Create link to a random GIF from Tenor found by the given keyword.
    :param updater: bot
    :param chat_id: chat to which to send a GIF
    :param keyword: GIF description
    """
    url = get(f'https://api.tenor.com/v1/random?q={keyword}&key={credentials.TENOR_TOKEN}&limit=1')\
        .json()['results'][0]['url']

    send_gif(updater, chat_id, url)


def send_gif(updater, chat_id, url):
    """
    Send GIF from url
    :param url: GIF url
    :param updater: bot
    :param chat_id: chat to which to send a GIF
    """
    try:
        updater.bot.send_animation(chat_id=chat_id, animation=url,
                                   duration=None, width=None, height=None, thumb=None, caption=None,
                                   parse_mode=None, disable_notification=False, reply_to_message_id=None,
                                   reply_markup=None, timeout=1)
    except:
        pass
