/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.fluidsynth.play;

import java.io.IOException;

import jorgan.disposition.event.OrganEvent;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.Chorus;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Reverb;
import jorgan.play.SoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class FluidsynthSoundPlayer extends SoundPlayer<FluidsynthSound> {

	private Fluidsynth synth;

	public FluidsynthSoundPlayer(FluidsynthSound sound) {
		super(sound);
	}

	@Override
	protected void setUp() {
		createSynth();
	}

	public void elementChanged(OrganEvent event) {
		if (event != null) {
			// TODO when necessary only 
			destroySynth();
			createSynth();
		}

		FluidsynthSound sound = getElement();
		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont", sound
					.getSoundfont());
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}
	}

	@Override
	protected void tearDown() {
		destroySynth();
	}

	@Override
	protected int getChannelCount() {
		FluidsynthSound sound = getElement();

		return sound.getChannels();
	}

	@Override
	protected boolean send(int channel, int command, int data1, int data2) {
		if (synth == null) {
			return false;
		}

		synth.send(channel, command, data1, data2);
		
		return true;
	}

	private void createSynth() {
		FluidsynthSound sound = getElement();

		removeProblem(Severity.ERROR, "soundfont");
		if (sound.getSoundfont() != null) {
			synth = new Fluidsynth(sound.getChannels());

			try {
				synth.soundFontLoad(sound.getSoundfont());
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", "soundfontLoad", sound
						.getSoundfont());
			}

			Reverb reverb = sound.getReverb();
			if (reverb == null) {
				synth.setReverbOn(false);
			} else {
				synth.setReverbOn(true);
				synth.setReverb(reverb.getRoomsize(), reverb.getDamping(),
						reverb.getWidth(), reverb.getLevel());
			}

			Chorus chorus = sound.getChorus();
			if (chorus == null) {
				synth.setChorusOn(false);
			} else {
				synth.setChorusOn(true);
				synth.setChorus(chorus.getNr(), chorus.getLevel(), chorus
						.getSpeed(), chorus.getDepthMs(), chorus.getType());
			}
		}
	}
	
	private void destroySynth() {
		if (synth != null) {
			synth.dispose();
			synth = null;
		}
	}	
}
