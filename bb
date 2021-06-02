#!/usr/bin/fish

# Setting this, so the repo does not need to be given on the commandline:

# some helpers and error handling:
function info
    printf "\n%s %s\n\n" (date) "$argv" >&2;
end

#trap 'echo $( date ) Backup interrupted >&2; exit 2' INT TERM

info "Starting backup"

# Backup the most important directories into an archive named after
# the machine this script is currently running on:
set BORG_REPO '/media/storage/backup/arch/'

sudo borg create                    \
    --verbose                       \
    --filter AME                    \
    --list                          \
    --stats                         \
    --show-rc                       \
    --compression lz4               \
    --exclude-caches                \
    --exclude '/home/*/.cache/*'    \
    --exclude '/var/tmp/*'          \
    --exclude '/var/cache/*'        \
    --exclude '/var/lock/*'         \
    --exclude '/var/run/*'          \
    --exclude '/var/spool/*'        \
    $BORG_REPO::{hostname}-{now:%Y-%m-%d_%H:%M:%S} \
    /etc                            \
    /home                           \
    /root                           \
    /var                            \
    /usr/local                      \

set backup_exit $status

info "Pruning repository"

# Use the `prune` subcommand to maintain 7 daily, 4 weekly and 6 monthly
# archives of THIS machine. The '{hostname}-' prefix is very important to
# limit prune's operation to this machine's archives and not apply to
# other machines' archives also:

borg prune                          \
    --list                          \
    --prefix '{hostname}-'          \
    --show-rc                       \
    --keep-daily    7               \
    --keep-weekly   4               \
    --keep-monthly  6               \

set prune_exit $status

# use highest exit code as global exit code
if test $backup_exit -gt $prune_exit
    set global_exit $backup_exit
else
    set global_exit $prune_exit
end

if test $global_exit -eq 0
    info "Backup and Prune finished successfully"
else if test $global_exit -eq 1
    info "Backup and/or Prune finished with warnings"
else
    info "Backup and/or Prune finished with errors"
end

exit $global_exit
