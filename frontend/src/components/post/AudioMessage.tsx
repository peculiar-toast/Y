import type { JSX } from "react";
import type { AudioMessageData } from "../../types/audioMessageData";

export function AudioMessage({ audioMessageData }: { audioMessageData: AudioMessageData }): JSX.Element {
    
    return (
	    <>
		<figure>
		    <figcaption>Messaggio audio</figcaption>
		    <audio controls src={audioMessageData.audioUrl}>
			
		    </audio>
		</figure>
	    </>
    )
}
