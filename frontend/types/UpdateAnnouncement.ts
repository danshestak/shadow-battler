export type UpdateAnnouncement = {
    title: string,
    content: string,
    date: Date
}

export const UpdateAnnouncements: UpdateAnnouncement[] = [
    {
        title: "Release",
        content: `
            Shadow Battler is finally publicly released! The site currently has counters for Team GO Rocket lineups from 
            the Harvest Festival: Taken Over event.
        `,
        date: new Date(2026, 11, 10)

    }
]