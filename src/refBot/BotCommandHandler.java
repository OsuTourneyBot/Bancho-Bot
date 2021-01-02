package refBot;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BotCommandHandler implements IRCEventHandler {

	private RefBot bot;
	private BotCommand currentCommand;
	private boolean waitingToStart = false;

	public BotCommandHandler(RefBot bot) {
		this.bot = bot;
		currentCommand = null;
	}

	public void setBotCommand(BotCommand command) {
		currentCommand = command;
	}

	public void setWaitingToStart(boolean state) {
		waitingToStart = state;
	}

	@Override
	public String[] match(String[] data) {
		return currentCommand == null ? null : currentCommand.getMatch(data);
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		if (currentCommand == BotCommand.READY) {
			bot.setPlayerState(data[0], 1);
			if (waitingToStart && bot.allReady()) {
				synchronized (bot) {
					bot.notify();
				}
			}
		} else if (currentCommand == BotCommand.BAN) {
			// Make sure the right person is actually banning
			if (bot.getWhoIsBanning().equalsIgnoreCase(data[0])) {
				bot.ban(data[1]);
			}
		} else if (currentCommand == BotCommand.PICK) {
			// Make sure the right person is actually picking
			if (bot.getWhoIsPicking().equalsIgnoreCase(data[0])) {
				bot.pick(data[1]);
			}
		} else {

		}
		return true;
	}

}
