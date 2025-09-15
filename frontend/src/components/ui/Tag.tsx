import type { JSX } from "react";
import type { TagData } from "../../types/tagData";

export function Tag({ tagData }: { tagData: TagData }): JSX.Element {
    return (
        <>
            <div className="tag">
                {tagData.name}
            </div>
        </>
    )
}