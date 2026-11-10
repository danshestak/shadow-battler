export type UpdateAnnouncement = {
    title: string,
    content: string,
    date: Date
}

export const UpdateAnnouncements: UpdateAnnouncement[] = [
    {
        title: "Release",
        content: `
            Shadow Battler is finally publicly released! The current counters are for 
            the Team GO Rocket lineups added during the Harvest Festival: Taken Over event.
        `,
        date: new Date(2026, 10, 10)
    }
]